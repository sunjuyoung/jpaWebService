package com.study.settings;

import com.study.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor//NullPointerException
public class Profile {

    @Length(max=40)
    private String bio;                //프로필 한줄소개

    private String url;

    @Length(max=60)
    private String occupation;

    @Length(max=60)
    private String location;

    private String profileImage;

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }
}
