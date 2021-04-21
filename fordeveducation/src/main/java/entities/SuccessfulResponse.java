package entities;

import lombok.*;

    @Builder
    @EqualsAndHashCode
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString

    public class SuccessfulResponse {
        private int code;
        private String type;
        private String message;
}
