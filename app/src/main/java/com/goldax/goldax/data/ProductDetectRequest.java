package com.goldax.goldax.data;

import java.io.File;

public class ProductDetectRequest {
    public File file; // require
    public String image_url; // require
    public float threshold; // 오차 검출용 not require
}
