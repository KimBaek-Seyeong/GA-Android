package com.goldax.goldax.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ProductDetectResult {
    public ResultData result;

    public class ResultData {
        public int width;
        public int height;
        public Product[] objects;

        public class Product {
            public float x1;
            public float x2;
            public float y1;
            public float y2;
            @SerializedName("class")
            public String name;

            @NonNull
            @Override
            public String toString() {
                return "name: " + name + " | x1: " + x1 + " | y1: " + y1 + " | x2: " + x2 + " | y2: " + y2;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return "width: " + width + " | height: " + height + " | objects: " + objects;
        }
    }
}
