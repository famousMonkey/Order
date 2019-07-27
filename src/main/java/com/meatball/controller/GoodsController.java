package com.meatball.controller;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.common.base.BaseController;
import com.meatball.common.base.PageEntity;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.ExcelImport;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.vo.ordervo.customize.RefundMutualInfo;
import com.meatball.common.vo.ordervo.customize.RefundStatistics;
import com.meatball.entity.GoodsRefund;
import com.meatball.service.BlanketService;
import com.meatball.service.DetailReturnService;
import com.meatball.service.GoodsService;
import com.meatball.vo.report.DailyReport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/orderservice")
@Api(tags = "退货单信息")
public class GoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private DetailReturnService detailService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlanketService blanketService;


    /***
     * 提交退款信息
     * @author: 张垒
     * @date: 2019/2/18
     */
    @PostMapping(value = "/goodsreturn/refundmutual")
    @ApiOperation(value = "提交退款信息")
    @TxcTransaction
    @Transactional
    public ResultMsg orderMutual(@RequestBody RefundMutualInfo refundMutualInfo){
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 提交退款 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println("接收数据："+ JSON.toJSONString(refundMutualInfo));
        String key = "orderRefund"+ refundMutualInfo.getOrderId();
        ResultMsg resultMsg = super.getErrorCode("订单提交失败");

        if (refundMutualInfo == null
                && StringUtils.isBlank(refundMutualInfo.getOrderId()) && refundMutualInfo.getType()==null
                && StringUtils.isBlank(refundMutualInfo.getStaffId()) && StringUtils.isBlank(refundMutualInfo.getSafePass())){
            resultMsg =  super.getCustomizeCode(21011,"提交信息错误");
        }else if(StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(key))){
            resultMsg =  super.getCustomizeCode(21009,"退款中，请稍候操作");
        }else {
            stringRedisTemplate.opsForValue().set(key, "0", 10, TimeUnit.SECONDS);
            int i = goodsService.refundMutual(refundMutualInfo);
            if(i == 0){
                resultMsg = super.getSuccessCode(blanketService.selectOrderMutual(refundMutualInfo.getOrderNo()));
            }else {
                switch (i) {
                    case 1:
                        resultMsg = super.getCustomizeCode(21001,"操作失败");
                        break;
                    case 2:
                        resultMsg = super.getCustomizeCode(21002,"信息错误");
                        break;
                    case 3:
                        resultMsg = super.getCustomizeCode(21003,"密码错误");
                        break;
                    case 4:
                        resultMsg = super.getCustomizeCode(21004,"无执勤信息");
                        break;
                    case 5:
                        resultMsg = super.getCustomizeCode(21005,"退款过期");
                        break;
                    case 6:
                        resultMsg = super.getCustomizeCode(21006,"已完成退款");
                        break;
                    case 7:
                        resultMsg = super.getCustomizeCode(21007,"未支付，不允许退款");
                        break;
                    case 8:
                        resultMsg = super.getCustomizeCode(21008,"数量超出，请检查退款数量");
                        break;
                    case 9:
                        resultMsg = super.getCustomizeCode(21008,"不允许单独退款");
                        break;
                    case 10:
                        resultMsg = super.getCustomizeCode(21008,"员工信息错误");
                        break;
                }
            }
        }

        stringRedisTemplate.delete(key);
        System.out.println("响应数据："+ JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 提交退款 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return resultMsg;
    }

    @GetMapping(value= "/goodsreturn/refundStatisticsByDutyId/{dutyId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = RefundStatistics.class)})
    @ApiOperation(value = "根据油枪ID获取订单统计")
    public ResultMsg queryOilOrderStatistics (@PathVariable("dutyId") String dutyId){
        if(StringUtils.isBlank(dutyId)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        RefundStatistics refundStatistics = detailService.queryRefundStatisticsByDutyId(dutyId);
        return super.getSuccessCode(refundStatistics);
    }

    /***
     *
     * @param: 根据id查询单个
     * @return: ResultMsg<GoodRefund>
     * @author: 周晓瀚
     * @date: 2018/11/12 15:40 
     */
    @GetMapping("/goodsreturn/{resourceId}")
    @ApiOperation(value = "根据id查询单个")
    public ResultMsg<GoodsRefund> getGoodsReturnById(@PathVariable("resourceId") String resourceId) {
        GoodsRefund goodsReturn = goodsService.selectResourceInfo(resourceId);
        if(goodsReturn == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(goodsReturn);
    }

    /***
     *分页查询信息
     * @param: [query]
     * @return: PageResultMsg
     * @author: 周晓瀚
     * @date: 2018/11/12 15:40
     */
    @GetMapping(value= "/goodsreturn/page")
    @ApiOperation(value = "分页查询信息")
    public PageResultMsg queryPage (PageEntity pageEntity, GoodsRefund goodsReturn){
        Map query= MapObjUtil.object2Map(goodsReturn);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<GoodsRefund> page = goodsService.queryPage(query);
        return super.getSuccessCode(page);
    }

    /***
     *查询列表
     * @param: [query]
     * @return: PageResultMsg
     * @author: 周晓瀚
     * @date: 2018/11/12 15:42
     */
    @ApiOperation(value="获取列表信息", notes="获取列表信息")
    @GetMapping(value = "/goodsreturn/list")
    public PageResultMsg queryList (GoodsRefund goodsReturn){
        Map query= MapObjUtil.object2Map(goodsReturn);
        List<GoodsRefund> list = goodsService.queryList(query);
        return super.getSuccessCode(list);
    }


    @PostMapping(value = "/goodsreturn/test")
    @TxcTransaction
    @Transactional
    public String orderMutual(Long time){
        return goodsService.test(time);
    }

    @PostMapping(value = "/goodsreturn/import")
    public PageResultMsg orderImport(@RequestParam(name = "fileName") MultipartFile multipartFile){
        Map<String, String> header = new HashMap<>();
        header.put("商品ID", "productId");
        header.put("编号", "productBarCode");
        header.put("名称", "productName");

        ExcelImport excelImport = new ExcelImport(multipartFile.getOriginalFilename());
        List<DailyReport> list;
        try {
            list = excelImport.readExcel(header, DailyReport.class, multipartFile.getInputStream());
        } catch (IOException e) {
            return super.getPageErrorCode("操作失败");
        }

        return super.getSuccessCode(list);
//        DailyReport
    }

//    @PostMapping(value = "/goodsreturn/test")
//    @TxcTransaction
//    @Transactional
//    public ResultMsg orderMutual(@RequestBody OilSaleRetreatInfoVo info){
//        return goodsService.test(info);
//    }

}
