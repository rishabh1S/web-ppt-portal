package com.example.webppt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.webppt.utils.ColorUtils;
import com.example.webppt.utils.SlideElementUtils;
import com.example.webppt.utils.SvgUtils;

@Service
public class ShapeGeneration {
    @Autowired
    SlideElementUtils slideElementUtils;
    @Autowired
    ColorUtils colorUtils;
    @Autowired
    SvgUtils svgUtils;

}
