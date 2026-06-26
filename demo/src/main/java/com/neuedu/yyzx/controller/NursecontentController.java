package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.yyzx.pojo.Nursecontent;
import com.neuedu.yyzx.pojo.Nurselevelitem;
import com.neuedu.yyzx.service.NursecontentService;
import com.neuedu.yyzx.service.NurselevelitemService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nursecontent")
@Tag(name = "护理内容管理")
public class NurseContentController {

    @Autowired
    private NursecontentService nursecontentService;

    @Autowired
    private NurselevelitemService nurselevelitemService;

    @Operation(summary = "查询所有护理内容")
    @GetMapping("/list")
    public ResultVo<List<Nursecontent>> list() {
        return ResultVo.ok(nursecontentService.list());
    }

    @Operation(summary = "新增护理内容")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nursecontent nursecontent) {
        boolean result = nursecontentService.save(nursecontent);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理内容")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nursecontent nursecontent) {
        boolean result = nursecontentService.updateById(nursecontent);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理内容")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nursecontentService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理内容")
    @GetMapping("/{id}")
    public ResultVo<Nursecontent> getById(@PathVariable Integer id) {
        return ResultVo.ok(nursecontentService.getById(id));
    }

    @Operation(summary = "查询护理内容关联的护理等级ID列表")
    @GetMapping("/{id}/levels")
    public ResultVo<List<Integer>> getLevels(@PathVariable Integer id) {
        LambdaQueryWrapper<Nurselevelitem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Nurselevelitem::getNurseContentId, id);
        List<Integer> levelIds = nurselevelitemService.list(wrapper).stream()
                .map(Nurselevelitem::getLevelId)
                .collect(Collectors.toList());
        return ResultVo.ok(levelIds);
    }

    @Operation(summary = "设置护理内容关联的护理等级（全量替换）")
    @PutMapping("/{id}/levels")
    public ResultVo<Object> setLevels(@PathVariable Integer id, @RequestBody List<Integer> levelIds) {
        // 先删除该内容的所有关联
        LambdaQueryWrapper<Nurselevelitem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Nurselevelitem::getNurseContentId, id);
        nurselevelitemService.remove(deleteWrapper);
        // 批量插入新关联
        for (Integer levelId : levelIds) {
            Nurselevelitem item = new Nurselevelitem();
            item.setLevelId(levelId);
            item.setNurseContentId(id);
            nurselevelitemService.save(item);
        }
        return ResultVo.ok();
    }
}
