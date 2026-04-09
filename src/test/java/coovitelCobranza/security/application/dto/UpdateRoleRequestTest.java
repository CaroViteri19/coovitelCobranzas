package coovitelCobranza.security.application.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateRoleRequestTest {

    @Test
    void shouldBindJsonToDto() throws Exception {
        String json = """
                {
                  "idUser": 2,
                  "role": [1, 2, 3, 4]
                }
                """;

        UpdateRoleRequest request = new ObjectMapper().readValue(json, UpdateRoleRequest.class);

        assertNotNull(request);
        assertEquals(2L, request.idUser());
        assertEquals(List.of(1L, 2L, 3L, 4L), request.role());
    }
}
