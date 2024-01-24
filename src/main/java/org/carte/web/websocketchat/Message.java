package org.carte.web.websocketchat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Message {
    private String from;
    private String to;
    private String content;

    //standard constructors, getters, setters
}