package com.jwt.secure_notes_app;

import com.jayway.jsonpath.JsonPath;
import com.jwt.secure_notes_app.enums.Role;
import com.jwt.secure_notes_app.models.Users;
import com.jwt.secure_notes_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional   // each test rolls back, so tests don't affect each other
class AuthAndOwnershipIntegrationTest {

    private static final String PASSWORD = "Test@12345";

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String tokenA;      // normal user A
    private String tokenB;      // normal user B
    private String tokenAdmin;  // admin

    @BeforeEach
    void setUp() throws Exception {
        createUser("userA", Role.USER);
        createUser("userB", Role.USER);
        createUser("adminX", Role.ADMIN);
        tokenA = login("userA");
        tokenB = login("userB");
        tokenAdmin = login("adminX");
    }

    // ---------- Authentication ----------

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("userA", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("userA", "wrong-password")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void noTokenReturns401() throws Exception {
        mockMvc.perform(get("/notes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/notes").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenCanCreateAndListNotes() throws Exception {
        createNote(tokenA, "A note");
        mockMvc.perform(get("/notes").header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("A note"));
    }

    @Test
    void userTokenCannotUseAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin/users").header("Authorization", bearer(tokenA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListUsersWithoutPasswords() throws Exception {
        mockMvc.perform(get("/admin/users").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].passWordHarsh").doesNotExist());
    }

    @Test
    void adminGets403OnNotes() throws Exception {
        mockMvc.perform(get("/notes").header("Authorization", bearer(tokenAdmin)))
                .andExpect(status().isForbidden());
    }

    // ---------- Ownership ----------

    @Test
    void eachUserSeesOnlyTheirOwnNotes() throws Exception {
        createNote(tokenA, "A note");
        createNote(tokenB, "B note");

        mockMvc.perform(get("/notes").header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("A note"));

        mockMvc.perform(get("/notes").header("Authorization", bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("B note"));
    }

    @Test
    void otherUserCannotReadUpdateOrDeleteMyNote() throws Exception {
        long noteIdA = createNote(tokenA, "A note");

        mockMvc.perform(get("/notes/" + noteIdA).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/notes/" + noteIdA)
                        .header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"hacked\",\"content\":\"x\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/notes/" + noteIdA).header("Authorization", bearer(tokenB)))
                .andExpect(status().isNotFound());

        // the owner still has the note, unchanged
        mockMvc.perform(get("/notes/" + noteIdA).header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("A note"));
    }

    @Test
    void ownerCannotBeForgedThroughRequestBody() throws Exception {
        mockMvc.perform(post("/notes")
                        .header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"forged\",\"content\":\"y\",\"owner\":\"userA\"}"))
                .andExpect(status().isCreated());

        // the note must belong to B, so A's list stays empty
        mockMvc.perform(get("/notes").header("Authorization", bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- Helpers ----------

    private void createUser(String username, Role role) {
        Users u = new Users();
        u.setUsername(username);
        u.setPassWordHarsh(passwordEncoder.encode(PASSWORD));
        u.setRole(role);
        userRepository.save(u);
    }

    private String login(String username) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(username, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private long createNote(String token, String title) throws Exception {
        String body = mockMvc.perform(post("/notes")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"content\":\"some content\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private String loginJson(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

