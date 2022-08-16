package com.springboot.blog.service.impl;

import com.springboot.blog.entity.post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        //Convert DTO to entity

        //post post = new post();
        //post.setTitle(postDto.getTitle());
        //post.setDescription(postDto.getDescription());

        //post.setContent(postDto.getContent());

        post post = maptoEntity(postDto);
        post newPost = postRepository.save(post);


        //Convert entity to DTO
        PostDto postResponse = mapToDTO(newPost);
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {


        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(pageNo,pageSize, sort);


        Page<post> posts = postRepository.findAll(pageable);

        List<post> listofPosts = posts.getContent();

        List<PostDto> content = listofPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;

    }

    @Override
    public PostDto getPostbyId(long id) {
        post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("post","id",id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {

        //Get post by id from the database

        post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("post","id",id));

        post.setTitle(postDto.getTitle());
        post.setDescription(post.getDescription());
        post.setContent(post.getContent());

        post updatedPOst = postRepository.save(post);
        return mapToDTO(updatedPOst);


    }

    @Override
    public void deletePostByID(long id) {

        post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("post","id",id));

        postRepository.delete(post);

    }

    //converted entity to CTO
    private PostDto mapToDTO(post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());
        return postDto;

    }

    private post maptoEntity(PostDto postDto) {

        post post = new post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        return post;


    }

}
