package com.meatball.controller;

import com.meatball.common.base.BaseController;
import com.meatball.common.base.PageEntity;
import com.meatball.common.base.PageInfo;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.utils.CopyUtils;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.entity.Productdetail;
import com.meatball.common.response.ResultMsg;
import com.meatball.service.ProdetailService;
import com.meatball.vo.ProductdetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:ProdetailController
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 13:58
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/orderservice")
@Api(tags = "商品订单明细")
public class ProdetailController extends BaseController {
    @Autowired
    private ProdetailService prodetailService;

    @GetMapping("/productdetail/{resourceId}")
    @ApiOperation(value = "根据id查询单个")
    public ResultMsg getStaffById(@PathVariable("resourceId") String resourceId) {
        Productdetail productdetail = prodetailService.selectResourceInfo(resourceId);
        if(productdetail == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(ProductdetailVo.class,productdetail));
    }

    /***
     *根据状态查询
     * @param: [pageNum, pageSize, prodeSta]
     * @return: com.meatball.core.response.PageResultMsg<org.springframework.data.domain.Page<Productdetail>>
     * @author: 周晓瀚
     * @date: 2018/11/3 14:04
     */
    @GetMapping(value= "/productdetail/page")
    @ApiOperation(value = "查询信息")
    public PageResultMsg queryPage (PageEntity pageEntity,Productdetail productdetail){
        Map query= MapObjUtil.object2Map(productdetail);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Productdetail> page = prodetailService.queryPage(query);
        PageInfo<ProductdetailVo> pageInfo = new PageInfo<>(CopyUtils.copyArray(ProductdetailVo.class,page.getContent()));
        pageInfo.setTotalElements(page.getTotalElements());
        return super.getSuccessCode(pageInfo);
    }
        /***
         *获取列表信息
         * @param: [query]
         * @return: PageResultMsg
         * @author: 周晓瀚
         * @date: 2018/11/8 9:08
         */
    @ApiOperation(value="获取列表信息", notes="获取列表信息")
    @GetMapping(value = "/productdetail/list")
    public PageResultMsg queryList (Productdetail productdetail){
        Map query= MapObjUtil.object2Map(productdetail);
        List<Productdetail> list = prodetailService.queryList(query);
        return super.getSuccessCode(CopyUtils.copyArray(ProductdetailVo.class,list));
    }
}
