package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.dto.BookResponse;
import com.maximebaron.springbook.book.dto.CreateBookRequest;
import com.maximebaron.springbook.book.dto.UpdateBookRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookEntity toEntity(CreateBookCommand command);
    BookResponse toBookResponse(BookEntity entity);
    CreateBookCommand toCreateBookCommand(CreateBookRequest request);
    UpdateBookCommand toUpdateBookCommand(UpdateBookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromCommand(UpdateBookCommand command, @MappingTarget BookEntity entity);
}
