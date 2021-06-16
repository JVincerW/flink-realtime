package com.vincer.app.func;

import com.alibaba.fastjson.JSONObject;
import com.vincer.utils.DimUtil;
import com.vincer.utils.ThreadPoolUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class DimAsyncFunction<T> extends RichAsyncFunction<T, T> implements DimJoinFunction<T> {

    //声明线程池对象
    private ThreadPoolExecutor threadPoolExecutor;

    //声明属性
    private final String tableName;

    public DimAsyncFunction(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void open(Configuration parameters) {
        //初始化线程池
        threadPoolExecutor = ThreadPoolUtil.getInstance();
    }

    @Override
    public void asyncInvoke(T input, ResultFuture<T> resultFuture) {

        threadPoolExecutor.submit(() -> {

            //0.获取查询条件
            String key = getKey(input);

            //1.查询维度信息
            JSONObject dimInfo = DimUtil.getDimInfo(tableName, key);

            //2.关联到事实数据上
            if (dimInfo != null && dimInfo.size() > 0) {
                try {
                    join(input, dimInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //3.继续向下游传输
            resultFuture.complete(Collections.singletonList(input));

        });

    }

}
