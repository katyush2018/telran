package telran.ashkelon2018.forum.service;

import java.util.List;

import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;

public interface ForumService {

	Post addNewPost(NewPostDto newPost);

	Post getPost(String id);

	Post removePost(String id);

	Post updatePost(PostUpdateDto postUpdateDto);

	boolean addLike(String id);

	Post addComment(String id, NewCommentDto newComment);

	Iterable<Post> findPostByTags(List<String> tags);

	Iterable<Post> findPostByAuthor(String author);

	Iterable<Post> findPostByDates(DatePeriodDto datesDto);

}
