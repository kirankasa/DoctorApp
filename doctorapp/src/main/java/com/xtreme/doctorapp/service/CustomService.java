package com.xtreme.doctorapp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.xtreme.doctorapp.domain.Doctor;

public class CustomService implements UserDetailsService {

	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMINISTRATOR = "ROLE_ADMIN";

	private ThreadLocal<User> currentUser = new ThreadLocal<User>();

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		try {
			if (username != null && !username.equals("admin")) {
				Collection<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();
				userAuthorities.add(new SimpleGrantedAuthority(ROLE_USER));

				List<Doctor> doctors = Doctor.findDoctorsByNameEquals(username)
						.getResultList();
				Doctor doctor = doctors.get(0);

				User user = new User(doctor.getName(), doctor.getName(), true,
						true, true, true, userAuthorities);
				currentUser.set(user);
				return user;
			}
			if (username != null && username.equals("admin")) {
				Collection<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();
				userAuthorities.add(new SimpleGrantedAuthority(ROLE_USER));
				userAuthorities.add(new SimpleGrantedAuthority(
						ROLE_ADMINISTRATOR));
				User user = new User("admin", "admin", true, true, true, true,
						userAuthorities);
				currentUser.set(user);
				return user;
			}
		} catch (Exception e) {
			throw new UsernameNotFoundException("Username " + username
					+ " not found!");
		}

		throw new UsernameNotFoundException("Username " + username
				+ " not found!");
	}
}
