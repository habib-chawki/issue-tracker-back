package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
@WithMockUser
@AutoConfigureRestDocs
public class IssueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    IssueService issueService;

    @SpyBean
    ModelMapper modelMapper;

    @SpyBean
    ObjectMapper objectMapper;

    Issue issue1, issue2;

    @BeforeEach
    public void init() {
        // set up issues
        issue1 = Issue.builder()
                .id(1L)
                .summary("Issue 1 summary")
                .description("Issue 1 description")
                .type(IssueType.STORY)
                .status(IssueStatus.RESOLVED)
                .creationTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .points(4)
                .build();

        issue2 = Issue.builder()
                .id(2L)
                .summary("Issue 2 summary")
                .description("Issue 2 description")
                .type(IssueType.TASK)
                .status(IssueStatus.IN_PROGRESS)
                .creationTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .points(8)
                .build();
    }

    @Test
    public void itShouldCreateIssue() throws Exception {

        // given the issue service response
        when(issueService.createIssue(eq(issue1), any(), anyLong())).thenReturn(issue1);

        // given the request body
        String requestBody = objectMapper.writeValueAsString(issue1);

        // when a POST request is made to create a new issue
        // then expect the response to be the created issue
        mockMvc.perform(post("/issues").param("project", "10")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody))
                .andDo(
                        document("{methodName}",
                                requestParameters(
                                        parameterWithName("project").description("The project id")
                                ),
                                requestFields(
                                        fieldWithPath("summary").description("The issue's summary"),
                                        fieldWithPath("description").description("The issue's description"),
                                        fieldWithPath("type").description("The issue's type, either Story, Bug or Task"),
                                        fieldWithPath("points").description("The issue's story points"),
                                        fieldWithPath("priority").description("The issue's priority, either High, Medium or Low"),
                                        fieldWithPath("assignee").description("The issue's assigned dev team member"),
                                        fieldWithPath("id").ignored(),
                                        fieldWithPath("status").ignored(),
                                        fieldWithPath("comments").ignored(),
                                        fieldWithPath("votes").ignored(),
                                        fieldWithPath("creationTime").ignored(),
                                        fieldWithPath("updateTime").ignored(),
                                        fieldWithPath("issueKey").ignored()

                                ),
                                responseFields(
                                        fieldWithPath("id").description("The issue's autogenerated id"),
                                        fieldWithPath("summary").description("The issue's summary"),
                                        fieldWithPath("description").description("The issue's description"),
                                        fieldWithPath("type").description("The issue's type, either Story, Bug or Task"),
                                        fieldWithPath("points").description("The issue's story points"),
                                        fieldWithPath("priority").description("The issue's priority, either High, Medium or Low"),
                                        fieldWithPath("assignee").description("The issue's assigned dev team member"),
                                        fieldWithPath("creationTime").description("The issue's creation time"),
                                        fieldWithPath("updateTime").description("The issue's update time"),
                                        fieldWithPath("status").ignored(),
                                        fieldWithPath("comments").ignored(),
                                        fieldWithPath("votes").ignored(),
                                        fieldWithPath("issueKey").ignored()

                                )
                        )
                );
    }

    @Test
    public void itShouldGetIssueById() throws Exception {
        // return an issue when getIssue service method is invoked
        when(issueService.getIssueById(2L)).thenReturn(issue2);

        // the response body is expected to contain the returned issue
        String responseBody = objectMapper.writeValueAsString(issue2);

        // expect the proper issue to have been returned when a get request is made
        mockMvc.perform(get("/issues/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseBody));
    }

    @Test
    public void givenGetIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() throws Exception {
        // given an error message
        String errorMessage = "Issue not found";

        // when the "getIssue()" service method throws a resource not found exception
        when(issueService.getIssueById(3L))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then an error message with a status code of 404 should be returned
        mockMvc.perform(get("/issues/3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void givenGetIssueById_whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() throws Exception {
        String errorMessage = "Invalid issue id";

        // expect an invalid issue id error
        mockMvc.perform(get("/issues/invalid"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldGetListOfAllIssues() throws Exception {
        // given a list of issues
        List<Issue> issues = Arrays.asList(issue1, issue2);

        // when issue service is invoked, return the list of issues
        when(issueService.getAllIssues()).thenReturn(issues);

        // perform get request and expect the list of all issues to have been returned
        mockMvc.perform(get("/issues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(issues)));
    }

    @Test
    public void itShouldUpdateIssue() throws Exception {
        // make a copy of the issue
        String issueJson = objectMapper.writeValueAsString(issue1);
        Issue updatedIssue = objectMapper.readValue(issueJson, Issue.class);

        // update the issue
        updatedIssue.setType(IssueType.BUG);
        updatedIssue.setSummary("Updated summary");

        // set up the updated issue as the request body
        String requestBody = objectMapper.writeValueAsString(updatedIssue);

        when(issueService.updateIssue(eq(1L), eq(updatedIssue), any())).thenReturn(updatedIssue);

        // when a put request to update an issue is made, then the response should be the updated issue
        mockMvc.perform(put("/issues/1")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(requestBody));
    }

    @Test
    public void givenUpdateIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() throws Exception {
        String errorMessage = "Issue not found";

        // when attempting to update an issue with an incorrect id
        when(issueService.updateIssue(eq(10L), eq(issue1), any()))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        String requestBody = objectMapper.writeValueAsString(issue1);

        // then a 404 not found error should be returned
        mockMvc.perform(put("/issues/10")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void givenUpdateIssueById_whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() throws Exception {
        String errorMessage = "Invalid issue id";
        String requestBody = objectMapper.writeValueAsString(issue1);

        // when the issue id is invalid then an 400 bad request error should be returned
        mockMvc.perform(put("/issues/invalid")
                .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldDeleteIssueById() throws Exception {
        doNothing().when(issueService).deleteIssue(eq(2L), any());

        mockMvc.perform(delete("/issues/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void givenDeleteIssueById_whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() throws Exception {
        String errorMessage = "Invalid issue id";

        mockMvc.perform(delete("/issues/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void givenDeleteIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() throws Exception {
        String errorMessage = "Issue not found";

        doThrow(new ResourceNotFoundException(errorMessage))
                .when(issueService).deleteIssue(eq(404L), any());

        // when an issue does not exists then a 404 error message should be returned
        mockMvc.perform(delete("/issues/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldUpdateIssueAssignee() throws Exception {
        // given the issue and user ids
        Long userId = 110L;

        // given the request body
        String requestBody = "{\"assignee\" : \"" + userId + "\"}";

        // given the service response
        when(issueService.updateIssueAssignee(issue1.getId(), userId)).thenReturn(issue1);

        // given the expected response (updated issue dto)
        String expectedResponse =
                objectMapper.writeValueAsString(modelMapper.map(issue1, IssueDto.class));

        // when a PATCH request is made to update the issue assignee
        // then the response should be the updated issue
        mockMvc.perform(patch("/issues/" + issue1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
