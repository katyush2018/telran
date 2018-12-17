package telran.ashkelon2018.forum.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;

@Service
public class ForumServiceImpl implements ForumService {
	@Autowired
	ForumRepository repository;

	@Override
	public Post addNewPost(NewPostDto newPost) {
		Post post = convertToPost(newPost);
		return repository.save(post);
	}

	private Post convertToPost(NewPostDto newPost) {
		return new Post(newPost.getTitle(), newPost.getContent(), newPost.getAuthor(), newPost.getTags());
	}

	@Override
	public Post getPost(String id) {
		return repository.findById(id).orElse(null);// нет поста, получай нал
	}

	@Override
	public Post removePost(String id) {
		Post post = repository.findById(id).orElse(null);
		if (post != null) {
			repository.delete(post);
		}
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto) {
		Post post = repository.findById(postUpdateDto.getId()).orElse(null);
		if (post != null) {
			post.setContent(postUpdateDto.getContent());
			repository.save(post);
		}
		return post;
	}

	@Override
	public boolean addLike(String id) {
		Post post = getPost(id);
		if (post == null)
			return false;
		post.addLike();
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment) {
		Post post = getPost(id);
		if (post == null)
			return null;
		post.addComment(convertNewCommentDtoToComment(newComment));
		repository.save(post);
		return post;
	}

	private Comment convertNewCommentDtoToComment(NewCommentDto newComment) {
		return new Comment(newComment.getMessage(),newComment.getUser());
	}

	@Override
	public Iterable<Post> findPostByTags(List<String> tags) {
		return repository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findPostByAuthor(String author) {
		return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostByDates(DatePeriodDto period) {
		return repository.findByDateCreatedBetween(LocalDate.parse(period.getFrom()), LocalDate.parse(period.getTo()));
	}

}
