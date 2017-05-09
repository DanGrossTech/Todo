package com.mindex.todo;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TodoItemRepository todoRepository;
	private static String baseApp="/todoItems";
	private static String sampleTitle="create to do list";
	private static String sampleDescription="Create a RESTful Web API that manages a to do list.";
	private static String sampleDueDate="2017-05-10T12:00:00.000+0000";
	private static String sampleRecord="{\"title\": \""+sampleTitle+
			"\", \"description\":\""+sampleDescription+
			"\",\"dueDate\": \""+sampleDueDate+"\"}";
	
	@Before
	public void deleteAllBeforeTests() throws Exception {
		todoRepository.deleteAll();
	}
	
	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.todoItems").exists());
	}

	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post(baseApp).content(
				sampleRecord)).andExpect(
						status().isCreated()).andExpect(
								header().string("Location", containsString(baseApp)));
	}

	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post(baseApp).content(sampleRecord)).andExpect(
						status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.title").value(sampleTitle)).andExpect(
						jsonPath("$.description").value(sampleDescription)).andExpect(
								jsonPath("$.dueDate").value(sampleDueDate));
	}

	@Test
	public void shouldQueryEntity() throws Exception {

		mockMvc.perform(post(baseApp).content(sampleRecord)).andExpect(
						status().isCreated());

		mockMvc.perform(
				get(baseApp+"/search/findByTitle?title={title}", sampleTitle)).andExpect(
						status().isOk()).andExpect(
								jsonPath("$._embedded.todoItems[0].title").value(
										sampleTitle));
	}

	@Test
	public void shouldUpdateEntity() throws Exception {

		String modifiedTitle="Make to do list";
		String modifiedRecord=sampleRecord.replace(sampleTitle, modifiedTitle);

		MvcResult mvcResult = mockMvc.perform(post(baseApp).content(sampleRecord)).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(put(location).content(modifiedRecord)).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.title").value(modifiedTitle)).andExpect(
						jsonPath("$.description").value(sampleDescription)).andExpect(jsonPath("$.dueDate").value(sampleDueDate));
	}

	@Test
	public void shouldPartiallyUpdateEntity() throws Exception {

		String modifiedTitle="Originate to do list";
		String modifiedRecord=sampleRecord.replace(sampleTitle, modifiedTitle);
		MvcResult mvcResult = mockMvc.perform(post(baseApp).content(sampleRecord)).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(
				patch(location).content(modifiedRecord)).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.title").value(modifiedTitle)).andExpect(
						jsonPath("$.description").value(sampleDescription)).andExpect(jsonPath("$.dueDate").value(sampleDueDate));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post(baseApp).content(sampleRecord)).andExpect(
				status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
}
