package jpabook.jpashop.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class MemberApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveMemberV2() throws Exception {

        MemberApiController.CreateMemberRequest createMemberRequest = new MemberApiController.CreateMemberRequest();
        createMemberRequest.setName("hwan2da");

        String requestJson = objectMapper.writeValueAsString(createMemberRequest);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v2/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("create-member-v2",
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("name").description("회원이름")
                        )
                ));
    }

    @Test
    void updateMemberV2() throws Exception{
    }

    @Test
    void membersV2() throws Exception{
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v2/members")
                ).andExpect(status().isOk())
                .andDo(document("get-member-v2"));
    }
}