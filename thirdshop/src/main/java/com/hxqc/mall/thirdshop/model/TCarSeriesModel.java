package com.hxqc.mall.thirdshop.model;


import java.util.ArrayList;

/**
 * Function:第三部店铺车系model
 *
 * @author 袁秉勇
 * @since 2015年12月05日
 */
public class TCarSeriesModel {
    public String brandName; // 品牌名

    public ArrayList< Series > series;


    public TCarSeriesModel(String brandName) {
        this.brandName = brandName;
    }

}
