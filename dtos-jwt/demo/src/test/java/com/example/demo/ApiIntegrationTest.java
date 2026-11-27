package com.example.demo;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.repo.appUserRepo;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    private static final String PRODUCTO_JSON = "{\"nombre\":\"Proteína de fresa\",\"descripcion\":\"2.2 kg\","
            + "\"lote\":\"123456\",\"precioUnitario\":1500.5,\"fechaCaducidad\":\"2026-05-04\"}";
    private static final String COMENTARIO_JSON = "{\"username\":\"ana\",\"comentario_corto\":\"Muy bueno\","
            + "\"estrellas\":5,\"comentario_largo\":\"Lo recomiendo\"}";
    private static final String IMAGEN_JSON = "{\"url\":\"https://ejemplo.com/a.png\",\"prioridad\":1,"
            + "\"tamanio\":\"mediana\",\"responsiva\":true}";
    private static final String CATEGORIA_JSON = "{\"nombre\":\"Suplementos\",\"descripcion\":\"Suplementos\","
            + "\"tipo\":\"salud\",\"icono\":\"icono.png\",\"imagenNavbar\":\"nav.png\"}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private appUserRepo users;

    // ---------- utilidades ----------

    private String nuevoUsername() {
        return "user-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String credenciales(String username) {
        return "{\"username\":\"" + username + "\",\"password\":\"clave-local-segura-123\"}";
    }

    private void registrar(String username) throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(username)))
                .andExpect(status().isCreated());
    }

    private String login(String username) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(body, "$.accessToken");
        return "Bearer " + token;
    }

    private String tokenUser() throws Exception {
        String username = nuevoUsername();
        registrar(username);
        return login(username);
    }

    private String tokenAdmin() throws Exception {
        String username = nuevoUsername();
        registrar(username);
        users.findByUsername(username).ifPresent(cuenta -> {
            cuenta.setRole("ADMIN");
            users.save(cuenta);
        });
        return login(username);
    }

    private String crearProducto(String bearerAdmin) throws Exception {
        String body = mockMvc.perform(post("/producto")
                .header("Authorization", bearerAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCTO_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/producto/")))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    // ---------- GET públicos ----------

    @Test
    void todosLosGetResponden_sinToken() throws Exception {
        for (String lista : new String[] { "/productos", "/categorias", "/comentarios", "/imagenes" }) {
            mockMvc.perform(get(lista)).andExpect(status().isOk());
        }
        for (String inexistente : new String[] {
                "/producto/no-existe", "/categoria/no-existe", "/categoria/producto/no-existe",
                "/comentario/no-existe", "/comentarios/no-existe",
                "/imagen/no-existe", "/imagenes/producto/no-existe" }) {
            mockMvc.perform(get(inexistente)).andExpect(status().isNotFound());
        }
    }

    // ---------- Seguridad ----------

    @Test
    void escrituraProtegida_sinToken_devuelve401() throws Exception {
        mockMvc.perform(post("/producto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCTO_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalido_devuelve401() throws Exception {
        mockMvc.perform(post("/producto")
                .header("Authorization", "Bearer token-invalido")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCTO_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void user_noPuedeEscribirProductosNiCategorias_devuelve403() throws Exception {
        String user = tokenUser();
        mockMvc.perform(post("/producto")
                .header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/categoria")
                .header("Authorization", user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CATEGORIA_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void userYAdmin_puedenEscribirComentariosEImagenes() throws Exception {
        for (String token : new String[] { tokenUser(), tokenAdmin() }) {
            mockMvc.perform(post("/comentario")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(COMENTARIO_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", startsWith("/comentario/")));
            mockMvc.perform(post("/imagen")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(IMAGEN_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", startsWith("/imagen/")));
        }
    }

    // ---------- Registro y login ----------

    @Test
    void registro_siempreCreaRolUser() throws Exception {
        String username = nuevoUsername();
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username
                        + "\",\"password\":\"clave-local-segura-123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void registro_usernameRepetido_devuelve409() throws Exception {
        String username = nuevoUsername();
        registrar(username);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(username)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_emiteJwtValido_yCredencialesInvalidasDan401() throws Exception {
        String username = nuevoUsername();
        registrar(username);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"incorrecta-123\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- DTOs, códigos HTTP y PATCH ----------

    @Test
    void dtoInvalido_devuelve400() throws Exception {
        String admin = tokenAdmin();
        mockMvc.perform(post("/producto")
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"\",\"descripcion\":\"x\",\"lote\":\"1\","
                        + "\"precioUnitario\":10,\"fechaCaducidad\":\"2026-05-04\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/comentario")
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ana\",\"comentario_corto\":\"ok\",\"estrellas\":9}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crudDeProductos_conCodigosHttpCorrectos() throws Exception {
        String admin = tokenAdmin();
        String id = crearProducto(admin);

        mockMvc.perform(get("/producto/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proteína de fresa"));

        mockMvc.perform(put("/producto/" + id)
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Otra\",\"descripcion\":\"1 kg\",\"lote\":\"9\","
                        + "\"precioUnitario\":99.5,\"fechaCaducidad\":\"2027-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Otra"));

        mockMvc.perform(put("/producto/no-existe")
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCTO_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/producto/" + id).header("Authorization", admin))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mockMvc.perform(delete("/producto/" + id).header("Authorization", admin))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/producto/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void patch_modificaSoloLosCamposEnviados() throws Exception {
        String admin = tokenAdmin();
        String id = crearProducto(admin);

        mockMvc.perform(patch("/producto/" + id)
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Nuevo nombre\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo nombre"))
                .andExpect(jsonPath("$.descripcion").value("2.2 kg"))
                .andExpect(jsonPath("$.lote").value("123456"));

        mockMvc.perform(patch("/producto/" + id)
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/producto/no-existe")
                .header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"x\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- OpenAPI ----------

    @Test
    void openApi_publicaEsquemaBearerJwt() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bearerAuth")))
                .andExpect(content().string(containsString("bearer")))
                .andExpect(content().string(containsString("JWT")));
    }
}
