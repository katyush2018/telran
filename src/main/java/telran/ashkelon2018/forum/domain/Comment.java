package telran.ashkelon2018.forum.domain;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = {"user", "dateCreated"})
@ToString
@Document(collection = "ForumFilter")
public class Comment {

	 String user;
	 @Setter
     String message;
     @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
     LocalDateTime dateCreated;
     int likes;
	public Comment(String user, String message) {
		super();
		this.user = user;
		this.message = message;
		dateCreated = LocalDateTime.now();		
	}
	
	public void addLike() {
		likes++;
	}
	
	
     
     
}
