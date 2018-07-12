package io.zensoft.share.service.facebook;

import io.zensoft.share.model.Vacancy;
import io.zensoft.share.model.VacancyResponse;
import io.zensoft.share.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagePostData;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FacebookPublisherService implements PublisherService {

    private String userAccessToken;
    private String pageAccessToken;

    private Facebook facebookUser;
    private Facebook facebookPage;
    private FacebookConfigs facebookConfigs;

    @Autowired
    public FacebookPublisherService(FacebookConfigs facebookConfigs){
        this.facebookConfigs = facebookConfigs;
    }

    @Override
    public VacancyResponse publish(Vacancy vacancy) {
        init(vacancy);
        if (isValidImageUrl(vacancy.getImage())) {
            return publishPhoto(vacancy);
        }
        return publishText(vacancy);
    }

    private void setPageAccessToken(String pageAccessToken) {
        this.pageAccessToken = pageAccessToken;
    }

    private void setPageAccessToken () {
        Map<String, String> uriVariables = new LinkedHashMap<>();
        ResponseEntity<Map> map = null;
        try {
            map = ((FacebookTemplate) facebookUser).getRestTemplate().exchange(
                    "https://graph.facebook.com/" + facebookConfigs.getUserId() + "/accounts",
                    HttpMethod.GET, (HttpEntity<?>) null, Map.class, (Object) uriVariables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object object = map.getBody().get("data");
        ArrayList<Map<String, Object>> pageList = (ArrayList<Map<String, Object>>) object;
        pageList.forEach( (page) -> {
            if(page.get("name").toString().contains("Zensoft")){
                setPageAccessToken(page.get("access_token").toString());
            }
        });
    }

    private boolean isValidImageUrl(String imageUrl) {
        Image image;
        try {
            image = ImageIO.read(new URL(imageUrl));
        } catch (Exception e) {
            return false;
        }
        return image != null;
    }

    private void init(Vacancy vacancy) {
        userAccessToken = vacancy.getFacebookUserAccessToken();
        facebookUser = new FacebookTemplate(userAccessToken, facebookConfigs.getAppNamespace(), facebookConfigs.getAppId());

        setPageAccessToken();
        facebookPage = new FacebookTemplate(pageAccessToken, facebookConfigs.getAppNamespace(), facebookConfigs.getAppId());
    }

    private VacancyResponse publishText(Vacancy vacancy) {
        PagePostData pagePostData = new PagePostData(facebookConfigs.getPageId());
        pagePostData = pagePostData.message(vacancy.getTitle());
        pagePostData.link(facebookConfigs.getLink(), null, null, null, null);
        facebookPage.pageOperations().post(pagePostData);
        return null;
    }

    private VacancyResponse publishPhoto(Vacancy vacancy) {
        String publishPhotoRequestUrl = getPublishPhotoRequestUrl(vacancy);
        Map<String, String> uriVariables = new LinkedHashMap<>();
        ResponseEntity<Map> map;
        try {
            map = ((FacebookTemplate) facebookPage).getRestTemplate().exchange( publishPhotoRequestUrl,
                    HttpMethod.POST, (HttpEntity<?>) null, Map.class, (Object) uriVariables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPublishPhotoRequestUrl(Vacancy vacancy) {
        String url = facebookConfigs.getPublishPhotoRequestUrlTemplate();
        url = url.replace("{pageId}", facebookConfigs.getPageId());
        url = url.replace("{photoUrl}", getPhotoUrl(vacancy));
        url = url.replace("{caption}", getText(vacancy));
        url = url.replace("{isPublished}", "true");
        return url;
    }

    private String getPhotoUrl(Vacancy vacancy) {
        return vacancy.getImage();
    }

    private String getText(Vacancy vacancy) {
        return vacancy.getTitle();
    }

}
