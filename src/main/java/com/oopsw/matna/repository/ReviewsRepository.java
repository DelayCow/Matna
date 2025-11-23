package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.Reviews;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends CrudRepository<Reviews, Integer> {

    @EntityGraph(attributePaths = {"author"})
    List<Reviews> findByRecipeAndDelDateIsNullOrderByInDateDesc(Recipe recipe);
}
