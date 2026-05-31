package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PagePayload<T> {
    private List<T> content;
    private Integer number;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
    private Boolean empty;
    private Integer numberOfElements;
}
