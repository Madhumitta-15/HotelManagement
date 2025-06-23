package com.hotel.Booking_System.config;

import com.hotel.Booking_System.service.userService.GuestUserDetailsService;
import com.hotel.Booking_System.service.userService.ManagerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private GuestUserDetailsService guestUserDetailsService;

    @Autowired
    private ManagerUserDetailsService managerUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173","http://localhost:5174")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(customizer -> customizer.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request

                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/guest/register").permitAll()

                        .requestMatchers("/guest/login").permitAll()
                        .requestMatchers("/manager/login").permitAll()
                        .requestMatchers("/admin/login").permitAll()


                        //HOTEL-PERMIT ALL
                        .requestMatchers("/hotel/gethotelslist").permitAll()
                        .requestMatchers("/hotel/findhotel/by-location").permitAll()
                        .requestMatchers("/hotel/findhotel/bylocationandroomtype").permitAll()
                        .requestMatchers("/hotel/gethotel/{hotelId}").permitAll()
                        .requestMatchers("/hotel/gethoteldetails/{hotelId}").permitAll()
                        //ROOM - PERMIT ALL
                        .requestMatchers("/rooms/getallrooms/{hotelId}").permitAll()
                        .requestMatchers("/rooms/getroom/{roomId}").permitAll()
                        .requestMatchers("/hotel/gethotel/roomtype/{roomType}").permitAll()

                        // REVIEW- PERMIT ALL
                        .requestMatchers("/review/hotelname/{hotelName}").permitAll()
                        .requestMatchers("/review/summary/{hotelId}").permitAll()
                        .requestMatchers("/review/{hotelId}").permitAll()
                        .requestMatchers("/review/summary/{hotelId}").permitAll()



                        .requestMatchers("/admin/dashboard").hasRole("ADMIN")



                        //MANAGER MANAGED BY ADMIN
                        .requestMatchers("/manager/addManager").hasRole("ADMIN")
                        .requestMatchers("/manager/deleteManager/{managerId}").hasRole("ADMIN")
                        .requestMatchers("/admin/viewallmanagers").hasRole("ADMIN")
                        .requestMatchers("/hotel/{hotelId}/assign-manager").hasRole("ADMIN")

                        //HOTEL MANAGED BY ADMIN
                        .requestMatchers("/hotel/addHotel").hasRole("ADMIN")
                        .requestMatchers("/hotel/gethotel/{id}").hasRole("ADMIN")
                        .requestMatchers("/hotel/updatehotel/{hotelId}").hasRole("ADMIN")
                        .requestMatchers("/hotel/deleteHotel/{hotelId}").hasRole("ADMIN")

                        //MANAGED BY BOTH MANAGER AND ADMIN
                        .requestMatchers("/admin/viewallguests").hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers("hotel/manager/{managerId}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/review/getreview/{hotelId}").hasAnyRole("ADMIN","MANAGER")


                        .requestMatchers("/manager/dashboard").hasRole("MANAGER")
                        .requestMatchers("/manager/managerprofile/{managerId}").hasRole("MANAGER")
                        .requestMatchers("/manager/updatemanager/{managerId}").hasRole("MANAGER")

                        //BOOKINGS MANAGED BY MANAGER
                        .requestMatchers("/bookings/room/{roomId}").hasRole("MANAGER")
                        .requestMatchers("/bookings/all").hasRole("MANAGER")
                        .requestMatchers("/bookings/confirmedbookings").hasRole("MANAGER")

                        //ROOM MANAGED BY MANAGER
                        .requestMatchers("/rooms/addroom/hotel/{hotelId}").hasRole("MANAGER")
                        .requestMatchers("/rooms/updateroom/{roomId}").hasRole("MANAGER")
                        .requestMatchers("/rooms/deleteroom/{roomId}").hasRole("MANAGER")


                        .requestMatchers("/guest/dashboard").hasRole("GUEST")
                        .requestMatchers("/guest/viewguest/{guestname}").hasRole("GUEST")
                        .requestMatchers("/guest/updateguest/{guestname}").hasRole("GUEST")
                        .requestMatchers("/guest/deleteGuest/{guestName}").hasRole("GUEST")
                        .requestMatchers("/guest/updateprofile").hasRole("GUEST")

                        //BOOKING MANAGED BY GUEST
                        .requestMatchers("/bookings/guest/{guestId}").hasRole("GUEST")
                        .requestMatchers("/bookings/bookroom").hasRole("GUEST")
                        .requestMatchers("/bookings/cancel/{bookingId}").hasRole("GUEST")
                        .requestMatchers("/bookings/check-review-eligibility/{hotelId}").permitAll()

                        //PAYMENTS MANAGED BY MANAGER
                        .requestMatchers("/payments/allpayments").hasRole("MANAGER")

                        //PAYMENTS VIEWED BY GUEST ID WITH ID
                        .requestMatchers("/payments/{paymentId}").hasRole("GUEST")
                        .requestMatchers("/loyalty/balance").hasRole("GUEST")

                        //REVIEW MANAGED BY GUEST
                        .requestMatchers("/review/{hotelId}/{reviewId}").hasRole("GUEST")
                        .requestMatchers("/review/add/{hotelId}").hasRole("GUEST")
                        .requestMatchers("/review/delete/{id}").hasRole("GUEST")
                        .requestMatchers("/review/all").hasRole("GUEST")
                        .requestMatchers("/review/filter/{hotelId}").permitAll()
                        .requestMatchers("/review/getbycategory/{hotelId}/{category}").permitAll()
                        .requestMatchers("/review/edit/{hotelId}").hasRole("GUEST")

                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    //setting in-memory admin username and password
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                org.springframework.security.core.userdetails.User.withUsername("admin")
                        .password(encoder.encode(("admin123")))
                        .roles("ADMIN")
                        .build()
        );
    }

    //Authentication provider for InMemory data
    @Bean
    public AuthenticationProvider inMemoryAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(inMemoryUserDetailsManager());
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }


    //Authentication provides for the data from the database
    @Bean
    public DaoAuthenticationProvider guestAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(guestUserDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider managerAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(managerUserDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }



}

