package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.config.UserConfigList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;

@Configuration
@EnableWebSecurity
@EnableScheduling
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SimpleBasicAuthenticationEntryPoint simpleBasicAuthenticationEntryPoint;

	@Value("classpath:config/users.xml")
	Resource usersXml;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/task/**").authenticated()
				.antMatchers("/").authenticated()
				.antMatchers("/user/new", "/client/language").permitAll()
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.authenticationEntryPoint(simpleBasicAuthenticationEntryPoint)
				.and()
				.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class)
				.logout()
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.and()
				.csrf().ignoringAntMatchers("/user/new", "/client/language");
	}

	@Bean
	@Override
	public CustomJdbcUserDetailsManager userDetailsService() {
		CustomJdbcUserDetailsManager users = new CustomJdbcUserDetailsManager(dataSource);
		if (!users.usersExists()) {
			String prefix = "{bcrypt}"; //Префикс, чтобы Spring знал, какой способ шифрования
			String salt = BCrypt.gensalt(); //Генерируем соль
			try (InputStream inputStream = usersXml.getInputStream()) {
				UserConfigList userConfigList = (UserConfigList) JAXBContext.newInstance(UserConfigList.class).createUnmarshaller().unmarshal(inputStream);
				for (UserConfig userConfig : userConfigList.getUsers()) {
					UserDetails user = User.builder()
							.username(userConfig.getLogin())
							.password(prefix + BCrypt.hashpw(userConfig.getPassword(), salt)) //Генерируем хеш пароля
							.roles(userConfig.getRoles().toArray(new String[0])) //Указываем роли пользователя
							.build();
					users.createUser(user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return users;
	}

}