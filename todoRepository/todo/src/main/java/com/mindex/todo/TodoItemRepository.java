package com.mindex.todo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;




public interface TodoItemRepository extends PagingAndSortingRepository<TodoItem, Long> {

		List<TodoItem> findByTitle(@Param("title") String title);

}
