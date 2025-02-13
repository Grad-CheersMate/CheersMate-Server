package CheersMate.cheersmate.domain.flask;

import CheersMate.cheersmate.domain.dto.FrontendRecommendationResponse;
import CheersMate.cheersmate.domain.dto.RecommendationResponse;
import CheersMate.cheersmate.domain.dto.WeatherRecommendationResponse;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponseTransformer {

    public static FrontendRecommendationResponse transform(RecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        FrontendRecommendationResponse.Data data = new FrontendRecommendationResponse.Data();

        // Request 변환
        FrontendRecommendationResponse.Request req = new FrontendRecommendationResponse.Request();
        req.setWeather(flaskResponse.getData().getRequest().getWeather());
        req.setEmotion(flaskResponse.getData().getRequest().getEmotion());
        req.setCompanion(flaskResponse.getData().getRequest().getCompanion());
        data.setRequest(req);

        // Recommend 단일 객체를 리스트로 변환
        FrontendRecommendationResponse.Recommend rec = new FrontendRecommendationResponse.Recommend();
        FrontendRecommendationResponse.Liquor liquor = new FrontendRecommendationResponse.Liquor();
        liquor.setName(flaskResponse.getData().getRecommend().getName());
        liquor.setVolume(flaskResponse.getData().getRecommend().getVolume());
        liquor.setType(flaskResponse.getData().getRecommend().getType());
        liquor.setImageUrl(flaskResponse.getData().getRecommend().getImageUrl());
        rec.setLiquor(liquor);

        List<FrontendRecommendationResponse.Recommend> recList = new ArrayList<>();
        recList.add(rec);
        data.setRecommend(recList);

        // Food 변환
        List<FrontendRecommendationResponse.Food> foodList = new ArrayList<>();
        flaskResponse.getData().getFood().forEach(flaskFood -> {
            FrontendRecommendationResponse.Food f = new FrontendRecommendationResponse.Food();
            f.setName(flaskFood.getName());
            f.setImageUrl(flaskFood.getImageUrl());
            foodList.add(f);
        });
        data.setFood(foodList);

        // Similar 변환
        List<FrontendRecommendationResponse.SimilarItem> similarList = new ArrayList<>();
        flaskResponse.getData().getSimilar().forEach(flaskSimilar -> {
            FrontendRecommendationResponse.SimilarItem si = new FrontendRecommendationResponse.SimilarItem();
            FrontendRecommendationResponse.Liquor sLiquor = new FrontendRecommendationResponse.Liquor();
            sLiquor.setName(flaskSimilar.getName());
            sLiquor.setVolume(flaskSimilar.getVolume());
            sLiquor.setType(flaskSimilar.getType());
            sLiquor.setImageUrl(flaskSimilar.getImageUrl());
            si.setLiquor(sLiquor);
            similarList.add(si);
        });
        data.setSimilar(similarList);

        frontendResponse.setData(data);
        return frontendResponse;
    }

    public static FrontendRecommendationResponse transformWeather(WeatherRecommendationResponse flaskResponse) {
        FrontendRecommendationResponse frontendResponse = new FrontendRecommendationResponse();
        frontendResponse.setResult(flaskResponse.isResult());
        frontendResponse.setHttpCode(flaskResponse.getHttpCode());
        frontendResponse.setError(flaskResponse.getError());

        FrontendRecommendationResponse.Data data = new FrontendRecommendationResponse.Data();

        // Request 변환 (날씨 전용)
        FrontendRecommendationResponse.Request req = new FrontendRecommendationResponse.Request();
        req.setWeather(flaskResponse.getData().getRequest().getWeather());
        data.setRequest(req);

        // Recommend 리스트 변환
        List<FrontendRecommendationResponse.Recommend> recList = new ArrayList<>();
        flaskResponse.getData().getRecommend().forEach(flaskRec -> {
            FrontendRecommendationResponse.Liquor liq = new FrontendRecommendationResponse.Liquor();
            liq.setName(flaskRec.getName());
            liq.setVolume(flaskRec.getVolume());
            liq.setType(flaskRec.getType());
            liq.setImageUrl(flaskRec.getImageUrl());
            FrontendRecommendationResponse.Recommend r = new FrontendRecommendationResponse.Recommend();
            r.setLiquor(liq);
            recList.add(r);
        });
        data.setRecommend(recList);

        // Food, Similar 없음
        data.setFood(null);
        data.setSimilar(null);

        frontendResponse.setData(data);
        return frontendResponse;
    }
}
