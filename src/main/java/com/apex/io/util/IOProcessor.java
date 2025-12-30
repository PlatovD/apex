package com.apex.io.util;

import com.apex.model.geometry.Model;

/**
 * Совершает операции перед/после загрузки или выгрузки модели.
 * Именно сюда поместил основные действия, которые должны выполняться единожды, при этих действиях.
 * Их нельзя добавлять в Pipeline, чтобы не портить производительность
 */
public interface IOProcessor {
    void process(Model model);
}
