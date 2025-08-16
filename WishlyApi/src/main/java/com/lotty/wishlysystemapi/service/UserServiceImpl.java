package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.entity.UserEntity;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        User savedUser = new User();
        BeanUtils.copyProperties(savedUserEntity, savedUser);
        return savedUser;
    }

    @Override
    public User updateUser(Long id, User user) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        BeanUtils.copyProperties(user, userEntity);
        UserEntity updatedUserEntity = userRepository.save(userEntity);
        User updatedUser = new User();
        BeanUtils.copyProperties(updatedUserEntity, updatedUser);
        return updatedUser;
    }

    @Override
    public boolean deleteUser(Long id) {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(userEntity -> {
                    User user = new User();
                    BeanUtils.copyProperties(userEntity, user);
                    return user;
                })
                .collect(Collectors.toList());
    }
}