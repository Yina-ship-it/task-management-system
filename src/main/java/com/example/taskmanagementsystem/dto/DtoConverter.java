package com.example.taskmanagementsystem.dto;

/**
 * @author Yina-ship-it
 * @since 09.12.2023
 */
public interface DtoConverter<Entity, Dto, Request, Response>{
    Dto convertEntityToDto(Entity entity);
    Entity convertDtoToEntity(Dto dto);
    Dto convertRequestToDto(Request request);
    Response convertDtoToResponse(Dto dto);
}
