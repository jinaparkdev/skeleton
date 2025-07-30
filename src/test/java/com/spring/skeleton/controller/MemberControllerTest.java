package com.spring.skeleton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.skeleton.common.Label;
import com.spring.skeleton.model.Member;
import com.spring.skeleton.model.MemberDetail;
import com.spring.skeleton.model.MembershipStatus;
import com.spring.skeleton.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    private final String name = "TestMember";
    private final String phone = "1234567890";
    private final Long membershipId = 1L;
    private final Instant startDate = Clock.systemDefaultZone().instant().plus(1, DAYS);
    private final String verificationCode = "123456";
    private final Boolean isRejoin = false;

    private final Member mockMember = new Member(
            1L,
            name,
            new Label<>("TestLabel", 1L),
            startDate,
            startDate.plus(1, DAYS),
            MembershipStatus.New
    );

    private final MemberDetail mockMemberDetail = new MemberDetail(
            mockMember,
            phone,
            verificationCode,
            startDate,
            startDate
    );

    @Test
    void create() throws Exception {
        MemberController.Body body = new MemberController.Body();
        body.setName(name);
        body.setPhone(phone);
        body.setMembershipId(membershipId);
        body.setStartDate(startDate);
        body.setVerificationCode(verificationCode);
        body.setIsRejoin(isRejoin);

        when(memberService.create(name, phone, membershipId, startDate, verificationCode))
                .thenReturn(mockMemberDetail);

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void verify() throws Exception {

        Long companyId = 1L;
        when(memberService.findByVerificationCode(verificationCode, companyId))
                .thenReturn(mockMember);

        mockMvc.perform(get("/member/verification/" + verificationCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
