/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

/**
 * <p>Entity storing facebook user information.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Entity
@DynamicUpdate
@Table(name = "fb_users")
public class FacebookUser extends LoggedUser {

    @Id
    private Long id;

    private String facebookId;

    private String firstName;
    private String lastName;
    private String name;
    private String email;
    private String coverPicture;
    private String picture;
    private String ageRangeMin;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /**
     * <p>Constructor for FacebookUser.</p>
     */
    public FacebookUser() {
    }

    /**
     * <p>Constructor for FacebookUser.</p>
     *
     * @param facebookId   a {@link java.lang.String} object.
     * @param firstName    a {@link java.lang.String} object.
     * @param lastName     a {@link java.lang.String} object.
     * @param name         a {@link java.lang.String} object.
     * @param email        a {@link java.lang.String} object.
     * @param coverPicture a {@link java.lang.String} object.
     * @param picture      a {@link java.lang.String} object.
     * @param ageRangeMin  a {@link java.lang.String} object.
     */
    public FacebookUser(String facebookId,
                        String firstName,
                        String lastName,
                        String name,
                        String email,
                        String coverPicture,
                        String picture,
                        String ageRangeMin) {
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.email = email;
        this.coverPicture = coverPicture;
        this.picture = picture;
        this.ageRangeMin = ageRangeMin;
    }

    /**
     * <p>Getter for the field <code>facebookId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFacebookId() {
        return facebookId;
    }

    /**
     * <p>Getter for the field <code>firstName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * <p>Setter for the field <code>firstName</code>.</p>
     *
     * @param firstName a {@link java.lang.String} object.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * <p>Getter for the field <code>lastName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * <p>Setter for the field <code>lastName</code>.</p>
     *
     * @param lastName a {@link java.lang.String} object.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link java.lang.String} object.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * <p>Getter for the field <code>coverPicture</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCoverPicture() {
        return coverPicture;
    }

    /**
     * <p>Setter for the field <code>coverPicture</code>.</p>
     *
     * @param coverPicture a {@link java.lang.String} object.
     */
    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * <p>Setter for the field <code>picture</code>.</p>
     *
     * @param picture a {@link java.lang.String} object.
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * <p>Getter for the field <code>ageRangeMin</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAgeRangeMin() {
        return ageRangeMin;
    }

    /**
     * <p>Setter for the field <code>ageRangeMin</code>.</p>
     *
     * @param ageRangeMin a {@link java.lang.String} object.
     */
    public void setAgeRangeMin(String ageRangeMin) {
        this.ageRangeMin = ageRangeMin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toAuditString() {
        return String.format("%s %s", name, email);
    }

    /**
     * <p>Getter for the field <code>user</code>.</p>
     *
     * @return a {@link com.openkoda.model.User} object.
     */
    public User getUser() {
        return user;
    }

    /**
     * <p>Setter for the field <code>user</code>.</p>
     *
     * @param user a {@link com.openkoda.model.User} object.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    public void setId(Long id) {
        this.id = id;
    }

}
