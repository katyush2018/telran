package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exception.UserConflictException;

@Service
public class AccountServiseImpl implements AccountService {
	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accounConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);// попросили декодировать токен
		if (userRepository.existsById(credentials.getLogin())) {// если существует репозиторий
			throw new UserConflictException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder().login(credentials.getLogin()).password(hashPassword)
				.firstName(userRegDto.getFirstName()).lastName(userRegDto.getLastName()).role("User")
				.expdate(LocalDateTime.now().plusDays(accounConfiguration.getExpPeriod())).build();
		userRepository.save(userAccount);
		return convertToUseProfileDto(userAccount);
	}

	private UserProfileDto convertToUseProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder().firstName(userAccount.getFirstName()).lastName(userAccount.getLastName())
				.login(userAccount.getLogin()).roles(userAccount.getRoles()).build();
	}

	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		userAccount.setFirstName(userRegDto.getFirstName());
		if (userRegDto.getLastName() != null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUseProfileDto(userAccount);
	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
//		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);
//		UserAccount user = userRepository.findById(credentials.getLogin()).get();
//		Set<String> roles = user.getRoles();
//		boolean hasRight = roles.stream()
//		.anyMatch(s -> "Admin".equals(s) || "Moderator".equals(s));
//		 hasRight = hasRight|| credentials.getLogin().equals(login);
//		 if(!hasRight) {
//			 throw new ForbiddenException();
//		 }
//		UserAccount userAccount = userRepository.findById(login)
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);
		if (userAccount != null && (userAccount.getRoles().contains("admin")
				|| userAccount.getRoles().contains("moderator") || login.equals(credentials.getLogin()))) {
			userRepository.delete(userAccount);
		}
		return convertToUseProfileDto(userAccount);
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount != null) {
			userAccount.addRole(role);
			userRepository.save(userAccount);
		} else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount != null) {
			userAccount.removeRole(role);
			userRepository.save(userAccount);
		} else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public void changePassword(String password, String token) {
		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		userAccount.setPassword(hashPassword);
		userAccount.setExpdate(LocalDateTime.now().plusDays(accounConfiguration.getExpPeriod()));
		userRepository.save(userAccount);

	}

	@Override
	public UserProfileDto login(String token) {
		AccountUserCredentials credentials = accounConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		return convertToUseProfileDto(userAccount);
	}

}
