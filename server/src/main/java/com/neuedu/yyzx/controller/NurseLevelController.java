package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.yyzx.pojo.Nurselevel;
import com.neuedu.yyzx.pojo.Nurselevelitem;
import com.neuedu.yyzx.service.NurselevelService;
import com.neuedu.yyzx.service.NurselevelitemService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nurselevel")
@Tag(name = "护理等级管理")
public class NurseLevelController {

    @Autowired
    private NurselevelService nurselevelService;

    @Autowired
    private NurselevelitemService nurselevelitemService;

    @Operation(summary = "查询所有护理等级")
    @GetMapping("/list")
    public ResultVo<List<Nurselevel>> list() {
        return ResultVo.ok(nurselevelService.list());
    }

    @Operation(summary = "新增护理等级")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nurselevel nurselevel) {
        boolean result = nurselevelService.save(nurselevel);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理等级")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nurselevel nurselevel) {
        boolean result = nurselevelService.updateById(nurselevel);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理等级")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nurselevelService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理等级")
    @GetMapping("/{id}")
    public ResultVo<Nurselevel> getById(@PathVariable Integer id) {
        return ResultVo.ok(nurselevelService.getById(id));
    }

    @Operation(summary = "查询护理等级关联的护理内容ID列表")
    @GetMapping("/{id}/contents")
    public ResultVo<List<Integer>> getContents(@PathVariable Integer id) {
        LambdaQueryWrapper<Nurselevelitem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nurselevelitem::getLevelId, id);
        List<Integer> contentIds = nurselevelitemService.list(wrapper).stream()
                .map(Nurselevelitem::getNurseContentId)
                .collect(Collectors.toList());
        return ResultVo.ok(contentIds);
    }

    @Operation(summary = "设置护理等级关联的护理内容（全量替换）")
    @PutMapping("/{id}/contents")
    public ResultVo<Object> setContents(@PathVariable Integer id, @RequestBody List<Integer> contentIds) {
        // 先删除该等级的所有关联
        LambdaQueryWrapper<Nurselevelitem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Nurselevelitem::getLevelId, id);
        nurselevelitemService.remove(deleteWrapper);
        // 批量插入新关联
        for (Integer contentId : contentIds) {
            Nurselevelitem item = new Nurselevelitem();
            item.setLevelId(id);
            item.setNurseContentId(contentId);
            nurselevelitemService.save(item);
        }
        return ResultVo.ok();
    }
}
