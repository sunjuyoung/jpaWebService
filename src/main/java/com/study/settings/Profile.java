package com.study.settings;

import com.study.domain.Account;
import lombok.Data;

@Data
public class Profile {
    private String bio;                //프로필 한줄소개

    private String url;

    private String occupation;

    private String location;            //varchar(255)

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
