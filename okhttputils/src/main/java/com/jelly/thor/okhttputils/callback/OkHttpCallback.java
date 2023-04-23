package com.jelly.thor.okhttputils.callback;

import com.jelly.thor.okhttputils.converters.IRefParamsType;
import com.jelly.thor.okhttputils.converters.fastjson.FastJsonResponseBodyConverter;
import com.jelly.thor.okhttputils.request.OkHttpRequest;

import okhttp3.Response;

/**
 * 类描述：当前项目数据初步解析 Gson写法<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/4/27 11:25 <br/>
 */
//public abstract class OkHttpCallback<T> extends Callback<T> {
//    @Override
//    public T parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception {
//        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
//        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//        Type typeArgument = actualTypeArguments[0];
//        boolean isPType = typeArgument instanceof ParameterizedType;
//        if (!isPType) {
//            //直接是泛型返回
//            Class<T> tClass = (Class<T>) typeArgument;
//            return ParseDataUtils.parseData(id, response, tClass);
//        }
//        ParameterizedType getPType = (ParameterizedType) typeArgument;
//        if (getPType.getRawType().toString().contains(ResponseModel.class.getCanonicalName())) {
//            //返回是ResponseModel<T>
//            GsonTypes.ParameterizedTypeImpl getPTypeImpl = new GsonTypes.ParameterizedTypeImpl(getPType.getOwnerType(), getPType.getRawType(), getPType.getActualTypeArguments());
//            return ParseDataUtils.<T>parseData(id, response, (Class<T>) ResponseModel.class, getPTypeImpl);
//        } else {
//            //返回是List<T>
//            GsonTypes.ParameterizedTypeImpl inTypeParamsImpl = new GsonTypes.ParameterizedTypeImpl(getPType.getOwnerType(), getPType.getRawType(), getPType.getActualTypeArguments());
//            GsonTypes.ParameterizedTypeImpl outTypeParamsImpl = new GsonTypes.ParameterizedTypeImpl(null, ResponseModel.class, inTypeParamsImpl);
//            return ParseDataUtils.parseData(id, response, null, outTypeParamsImpl);
//        }
//    }
//}

//fastJson写法
public abstract class OkHttpCallback<T> extends Callback<T> implements IRefParamsType<T> {
    @Override
    public T parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception {
        FastJsonResponseBodyConverter<T> responseBodyConverter = new FastJsonResponseBodyConverter<>();
        T parseData = responseBodyConverter.converter(id, this, response.request(), response, null);
        return parseData;
    }
}