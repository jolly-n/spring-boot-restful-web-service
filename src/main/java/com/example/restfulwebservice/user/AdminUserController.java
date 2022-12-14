package com.example.restfulwebservice.user;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final UserDaoService service;

    @GetMapping("/users")
    public MappingJacksonValue retrieveAllUsers() {
        List<User> users = service.findAll();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "joinDate", "password");

        SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter); // 필터 생성

        MappingJacksonValue mapping = new MappingJacksonValue(users); // 데이터 변환
        mapping.setFilters(filters); // 필터링 적용

        return mapping;
    }

    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv1+json")
    public MappingJacksonValue retrieveUserV1(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%S] not found", id));
        }

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "joinDate", "ssn");

        SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("UserInfo", filter); // 필터 생성

        MappingJacksonValue mapping = new MappingJacksonValue(user); // 데이터 변환
        mapping.setFilters(filters); // 필터링 적용

        return mapping;
    }

    @GetMapping(value = "/users/{id}", produces = "application/vnd.company.appv2+json")
    public MappingJacksonValue retrieveUserV2(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%S] not found", id));
        }

        // User -> User2
        UserV2 userV2 = new UserV2();
        BeanUtils.copyProperties(user, userV2);
        userV2.setGrade("VIP");

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "joinDate", "grade");

        SimpleFilterProvider filters = new SimpleFilterProvider().addFilter("UserInfoV2", filter); // 필터 생성

        MappingJacksonValue mapping = new MappingJacksonValue(userV2); // 데이터 변환
        mapping.setFilters(filters); // 필터링 적용

        return mapping;
    }

}
