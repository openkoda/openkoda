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

package com.openkoda.core.helper;

import com.google.gson.internal.LinkedTreeMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openkoda.core.helper.NameHelper.getClasses;
import static com.openkoda.model.Privilege.*;
import static java.util.stream.Collectors.toList;

/**
 *
 * <p>PrivilegeHelper class</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Component("auth")
public class PrivilegeHelper implements HasSecurityRules {

    @Value("${authentication.loginAndPassword:true}")
    public boolean loginAndPasswordAuthentication;
    @Value("${login.sign-up.link:false}")
    public boolean signUpLink;
    @Value("${application.classes.privileges-enum:}")
    private String[] privilegesEnumClasses;

    private static final Map<String, Enum> nameToEnum = new LinkedTreeMap<>();
    private static final Set<Enum> adminPrivileges = new HashSet<>(Arrays.asList(Privilege.values()));
    private static final Set<Enum> userPrivileges = new HashSet<>();
    private static final Set<Enum> orgAdminPrivileges = new HashSet<>();
    private static final Set<Enum> orgUserPrivileges = new HashSet<>();
    private static PrivilegeHelper instance;

    public final static PrivilegeHelper getInstance() {
        return instance;
    }


    public Long getCurrentUserId() {
        return UserProvider.getFromContext().map( a-> a.getUser() ).map( a -> a.getId() ).orElse(null );
    }

    public Long getCurrentOrganizationId() {
        return UserProvider.getFromContext().map( a-> a.getDefaultOrganizationId() ).orElse(null );
    }

    public static Enum[] fromJoinedStringToArray(String joinedPrivileges) {
        return Arrays.stream(joinedPrivileges.split(",")).map(PrivilegeHelper::valueOfString).toArray(Enum[]::new);
    }

    public static Set<Enum> fromJoinedStringToSet(String joinedPrivileges) {
        return Arrays.stream(joinedPrivileges.split(",")).map(PrivilegeHelper::valueOfString).collect(Collectors.toUnmodifiableSet());
    }

    public static Set<String> fromJoinedStringToStringSet(String joinedPrivileges) {
        return streamSplitAndRemoveParenthesis(joinedPrivileges).collect(Collectors.toUnmodifiableSet());
    }

    //TODO: this method is handy not only for privileges, move to some more general helper
    public static Stream<String> streamSplitAndRemoveParenthesis(String joinedPrivileges) {
        return Arrays.stream(splitAndRemoveParenthesis(joinedPrivileges));
    }

    public static Set<String> fromPrivilegesToStringSet(PrivilegeBase... p) {
        return Arrays.stream(p).map(a -> a.name()).collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Enum> fromJoinedStringInParenthesisToPrivilegeEnumSet(String joinedValuesInParenthesis) {
        Stream<String> k = streamSplitAndRemoveParenthesis(joinedValuesInParenthesis);
        return k.map(PrivilegeHelper::valueOfString).collect(Collectors.toUnmodifiableSet());
    }


    public static Set<Long> fromJoinedStringInParenthesisToLongSet(String joinedValuesInParenthesis) {
        return streamSplitAndRemoveParenthesis(joinedValuesInParenthesis).map(Long::valueOf).collect(Collectors.toUnmodifiableSet());
    }

    public static Enum valueOfString(String s) {
        return nameToEnum.get(s);
    }


    /**
     * Separates words in parentheses and removes them
     * @param joinedPrivileges
     * @return an array of strings without parentheses
     */
    private static String[] splitAndRemoveParenthesis(String joinedPrivileges) {
        return StringUtils.isEmpty(joinedPrivileges) ? new String[0]
                : joinedPrivileges.substring(1, joinedPrivileges.length() - 1).split("\\),\\(");
    }

    /**
     * @param e
     * @return String in parentheses
     */
    public static String inParenthesis(Enum e) {
        return "(" + e.name() + ")";
    }


    /**
     * @param privilegesSet
     * @return joined string in parentheses
     */
    public static String toJoinedStringInParenthesis(Set<Enum> privilegesSet) {
        return StringUtils.join(privilegesSet.stream().map(PrivilegeHelper::inParenthesis).collect(toList()), ',');
    }

    /**
     * @param privileges
     * @return joined string in parentheses
     */
    public static String toJoinedStringInParenthesis(Enum ... privileges) {
        return StringUtils.join(Stream.of(privileges).map(PrivilegeHelper::inParenthesis).collect(toList()), ',');
    }

    public static List<Enum> allEnumsToList() {
        return new ArrayList<>(nameToEnum.values());
    }

    public static Map<String, Enum> getNameToEnum() {
        return nameToEnum;
    }

    /**
     * @return map of all enums as privilege base list
     */
    public static List<PrivilegeBase> allEnumsAsPrivilegeBaseList() {
        List<PrivilegeBase> result = new ArrayList<>();
        for (Map.Entry e: nameToEnum.entrySet()) {
            PrivilegeBase pb = (PrivilegeBase) e.getValue();
            if (pb.isHidden()) { continue; }
            result.add(pb);
        }
        return result;
    }

    public static PrivilegeBase[] allEnumsAsPrivilegeBase() {
        return allEnumsAsPrivilegeBaseList().toArray(PrivilegeBase[]::new);
    }

    /**
     * @return map of all enums as privilege base linked map
     */
    public static Map<PrivilegeBase, String> allEnumsAsPrivilegeBaseLinkedMap() {
        TreeMap<PrivilegeBase, String> result = new TreeMap<>(Comparator.comparing(PrivilegeBase::getLabel));
        for (Map.Entry e: nameToEnum.entrySet()) {
            PrivilegeBase pb = (PrivilegeBase) e.getValue();
            if (pb.isHidden()) { continue; }
            result.put(pb, pb.getLabel());
        }
        return result;
    }

    public static String allEnumsAsPrivilegeBaseJsonString() throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (var e : nameToEnum.entrySet()) {
            PrivilegeBase pb = (PrivilegeBase) e.getValue();
            result = new JSONObject();
            result.put("k", pb.name());
            result.put("v", pb.getLabel());
            result.put("hidden", String.valueOf(pb.isHidden()));
            results.put(result);
        }
        return results.toString();
    }

    /**
     * Static method which add all enum values to nameToEnum map
     *
     * @param enumClasses
     */
    public static void registerEnumClasses(Class<Enum>[] enumClasses) {
        for (Class<Enum> ec : enumClasses) {
            List<Enum> enums = Arrays.stream(ec.getEnumConstants()).sorted(Comparator.comparing(o -> ((PrivilegeBase) o).getLabel())).collect(toList());
            for (Enum e : enums) {
                nameToEnum.put(e.name(), e);
            }
        }
    }

    public static Set<Enum> getAdminPrivilegeSet() {
        return adminPrivileges;
    }

    public static Set<Enum> getOrgAdminPrivilegeSet() {
        return orgAdminPrivileges;
    }

    public static Set<Enum> getUserPrivilegeSet() {
        return userPrivileges;
    }

    public static Set<Enum> getOrgUserPrivilegeSet() {
        return orgUserPrivileges;
    }

    public static Set<String> getAdminPrivilegeStrings() {
        return fromPrivilegesToStringSet(allEnumsAsPrivilegeBase());
    }

    public static Privilege[] getAdminPrivileges() {
        return Privilege.values();
    }

    /**
     * a post constructor that initializes the attributes of the class
     */
    @PostConstruct
    void init() {
        registerEnumClasses((Class<Enum>[]) getClasses(privilegesEnumClasses));
        instance = this;
        orgAdminPrivileges.addAll(adminPrivileges);
        orgAdminPrivileges.removeAll(Arrays.asList(canAccessGlobalSettings,canImpersonate,canSeeUserEmail,canResetPassword,canChangeEntityOrganization, canEditAttributes, canEditUserAttributes));
        orgUserPrivileges.addAll(Arrays.asList(readUserData, readOrgData));
        userPrivileges.addAll(Arrays.asList(canEditUserAttributes));
    }


}
