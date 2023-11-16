package ru.bmstu.gatewayservice.dto.wrapper;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class PageableWrapperDto<T> {

    private List<T> items;

    private int page;
    private int pageSize;
    private int totalElements;

}
