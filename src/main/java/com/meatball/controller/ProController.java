package com.meatball.controller;

import com.alibaba.fastjson.JSON;
import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseController;
import com.meatball.common.base.PageEntity;
import com.meatball.common.base.PageInfo;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.CopyUtils;
import com.meatball.common.utils.DateUtil;
import com.meatball.common.utils.ExcelExport;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.vo.ordervo.customize.ProSalesStatistics;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.entity.Productorder;
import com.meatball.service.PorderService;
import com.meatball.service.ProdetailService;
import com.meatball.vo.ProductorderVo;
import com.meatball.vo.report.DailyReport;
import com.meatball.vo.report.OilsorderReport;
import com.meatball.vo.report.ProductorderReport;
import com.meatball.vo.report.ProductorderReportRe;
import com.meatball.vo.request.ProductorderRe;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ProController
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 11:18
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/orderservice")
@Api(tags = "商品订单信息")
public class ProController extends BaseController {
    @Autowired
    private PorderService porderService;
    @Autowired
    private ProdetailService prodetailService;
    @Autowired
    private ResourceClient resourceClient;

    @GetMapping("/productorder/{resourceId}")
    @ApiOperation(value = "根据id查询单个")
    public ResultMsg<ProductorderVo> getProductorderById(@PathVariable("resourceId") String resourceId) {
        Productorder productorder = porderService.selectResourceInfo(resourceId);
        if(productorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(ProductorderVo.class,productorder));
    }

    @GetMapping(value= "/productorder/page")
    @ApiOperation(value = "查询分页列表")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = Productorder.class)})
    public PageResultMsg queryPage (PageEntity pageEntity,ProductorderRe productorder){
        Map query= MapObjUtil.object2Map(productorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Productorder> page = porderService.queryPage(query);
        PageInfo<ProductorderVo> pageInfo = new PageInfo<>(CopyUtils.copyArray(ProductorderVo.class,page.getContent()));
        pageInfo.setTotalElements(page.getTotalElements());
        return super.getSuccessCode(pageInfo);
    }

    @ApiOperation(value="查询列表")
    @GetMapping(value = "/productorder/list")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = Productorder.class)})
    public PageResultMsg queryList (ProductorderRe productorder){
        Map query= MapObjUtil.object2Map(productorder);
        List<Productorder> list = porderService.queryList(query);
        return super.getSuccessCode(CopyUtils.copyArray(ProductorderVo.class,list));
    }

    @GetMapping(value= "/productorder/report/page")
    @ApiOperation(value = "查询报表分页列表")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = ProductorderReport.class)})
    public PageResultMsg queryReportPage (PageEntity pageEntity, ProductorderReportRe productorder){
        String[] refundSta=null;
        if( productorder.getRefundSta() != null &&  productorder.getRefundSta().split(",").length>1){
            refundSta = productorder.getRefundSta().split(",");
            productorder.setRefundSta(null);
        }

        Map query= MapObjUtil.object2Map(productorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        if(refundSta != null){
            for (int i = 0;i<refundSta.length; i++){
                query.put("refundSta["+i+"]",refundSta[i]);
            }
        }
        return resourceClient.productprderPage(query);
    }

    @ApiOperation(value="查询报表列表")
    @GetMapping(value = "/productorder/report/list")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = ProductorderReport.class)})
    public PageResultMsg queryReportList (ProductorderReportRe productorder){
        if(productorder.getRefundSta() != null && productorder.getRefundSta().split(",").length>0){
            productorder.setRefundSta(JSON.toJSONString(productorder.getRefundSta().split(",")));
        }
        if(productorder.getProSta() != null && productorder.getProSta().split(",").length>0){
            productorder.setProSta(JSON.toJSONString(productorder.getProSta().split(",")));
        }
        Map query= MapObjUtil.object2Map(productorder);
        return resourceClient.productprderList(query);
    }

    @GetMapping(value= "/productorder/salesstatistics")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "商品订单列表统计")
    public ResultMsg querySalesStatistics (ProductorderRe productorder ){
        Map query = MapObjUtil.object2Map(productorder);
        SalesStatistics salesStatistics = porderService.querySalesStatistics(query);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping("productorder/byblanketid/{blanketOrderId}")
    @ApiOperation(value = "根据总订单Id信息查询")
    public ResultMsg<ProductorderVo> getProductByblanketOrderId(@PathVariable("blanketOrderId") String blanketOrderId) {
        Productorder productorder;
        try {
            productorder = porderService.selectProductByblanketOrderId(blanketOrderId);

        } catch (Exception e) {
            return super.getErrorCode("操作失败");
        }
        if(productorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(ProductorderVo.class,productorder));
    }

    @GetMapping(value= "/productorder/salesstatisticsbydutyid/{dutyId}")
    @ApiOperation(value = "根据班组执勤ID查询销售统计")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    public ResultMsg querySalesStatistics (@PathVariable("dutyId") String dutyId,String payment){

        if(StringUtils.isBlank(dutyId)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        SalesStatistics salesStatistics = porderService.querySalesStatisticsByDutyId(dutyId,payment);
        return super.getSuccessCode(salesStatistics);
    }

    @ApiOperation(value="非油品订单导出")
    @GetMapping(value = "/productorder/export")
    public void exportExcel(ProductorderReportRe productorder, HttpServletResponse response) {
        String tableName= DateUtil.getAllTime();

        String[] refundSta=null;
        if( productorder.getRefundSta() != null &&  productorder.getRefundSta().split(",").length>1){
            refundSta = productorder.getRefundSta().split(",");
            productorder.setRefundSta(null);
        }
        Map query= MapObjUtil.object2Map(productorder);
        if(refundSta != null){
            for (int i = 0;i<refundSta.length; i++){
                query.put("refundSta["+i+"]",refundSta[i]);
            }
        }

        List<OilsorderReport> list = porderService.queryProductorderReportList(query);
        Map<String, String> header = new LinkedHashMap<>();
        header.put("ProductorderNo", "订单编号");
        header.put("OrderGenerationTime", "交易时间");
        header.put("MemberPhone", "手机号");
        header.put("TextPaymentMethod", "支付方式");
        header.put("CopeWith", "实付金额(元)");
        header.put("Discounts", "优惠金额(元)");
        header.put("ProMoney", "订单金额(元)");
        header.put("TextOrderStatus", "状态");

        ExcelExport export = new ExcelExport(tableName);
        try {
            export.writeExcel(list,header,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value="非油品日报表")
    @GetMapping(value = "/productorder/dailyreport/{oilStationId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(集合)", response = DailyReport.class)})
    public PageResultMsg dailyReport (@PathVariable("oilStationId")  String oilStationId,
                                      PageEntity pageEntity,String payment,String squadId, String date){
        PageInfo<DailyReport> pageInfo = porderService.queryDailyReport(pageEntity,oilStationId,squadId,date);
        return super.getSuccessCode(pageInfo);
    }

    @ApiOperation(value="非油品日报表导出")
    @GetMapping(value = "/productorder/dailyreport/export/{oilStationId}")
    public void exportExcelDailyReport(@PathVariable("oilStationId") String oilStationId,String payment,String squadId, String date ,HttpServletResponse response) {
        String tableName= DateUtil.getAllTime();
        Map<String,List<DailyReport>> dailyReportMap = porderService.queryDailyReport(oilStationId,squadId,date);
        ExcelExport export = new ExcelExport(tableName);
        for (String key: dailyReportMap.keySet()){
            Map<String, String> header = new LinkedHashMap<>();
            header.put("Other", "日期："+date+"\r\n班次名称：" + (key.equals("Sheet1")?"":key)+", ");
            header.put("ProductBarCode", "便利店销售报表,商品编号");
            header.put("ProductName", "便利店销售报表,商品名称");
            header.put("Quantity", "便利店销售报表,销售数量");
            header.put("Total", "便利店销售报表,销售金额");
            if(StringUtils.isBlank(payment) || payment.equals("11") || payment.equals("1")){
                header.put("Cash", "便利店销售报表,现金");
            }
            if(StringUtils.isBlank(payment) || payment.equals("2") || payment.equals("21")) {
                header.put("Swipe", "便利店销售报表,银行卡");
            }
            if(StringUtils.isBlank(payment) || payment.equals("31")) {
                header.put("Wechat", "便利店销售报表,微信");
            }
            if(StringUtils.isBlank(payment) || payment.equals("32")) {
                header.put("Alipay", "便利店销售报表,支付宝");
            }
            if(StringUtils.isBlank(payment) || payment.equals("40")) {
                header.put("CommonAccount", "便利店销售报表,通用账户");
            }
//            if(StringUtils.isBlank(payment) || payment.equals("41")) {
//                header.put("GasolineAccount", "便利店销售报表,汽油账户");
//            }
//            if(StringUtils.isBlank(payment) || payment.equals("42")) {
//                header.put("DieselAccount", "便利店销售报表,柴油账户");
//            }
//            if(StringUtils.isBlank(payment) || payment.equals("43")) {
//                header.put("CngAccount", "便利店销售报表,CNG账户");
//            }
            if(StringUtils.isBlank(payment) || payment.equals("5") || payment.equals("51")) {
                header.put("Iou", "便利店销售报表,白条");
            }
            export.putExcelShett(key,dailyReportMap.get(key),header);
        }
        try {
            export.writeExcel(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value= "/productorder/oilstationconsumption/{relatedId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "查询油站销售统计信息")
    public ResultMsg queryOilStationConsumption (@PathVariable("relatedId") String relatedId,
                                                 String startDate,String endDate){
        if(StringUtils.isBlank(relatedId)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        if(StringUtils.isNotBlank(startDate) && !DateUtil.isDateString(startDate)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        if(StringUtils.isNotBlank(endDate) && !DateUtil.isDateString(endDate)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        SalesStatistics salesStatistics = porderService.queryConsumptionByRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/productorder/proalesstatistics/{relatedId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = ProSalesStatistics.class)})
    @ApiOperation(value = "查询商品统计信息")
    public PageResultMsg queryProSalesStatistics (@PathVariable("relatedId") String relatedId,
                                               String startDate,String endDate){
        if(StringUtils.isBlank(relatedId)){
            return super.getPageErrorCode("查询失败");
        }
        if(StringUtils.isNotBlank(startDate) && !DateUtil.isDateString(startDate)){
            return super.getPageErrorCode("查询失败");
        }
        if(StringUtils.isNotBlank(endDate) && !DateUtil.isDateString(endDate)){
            return super.getPageErrorCode("查询失败");
        }
        List<ProSalesStatistics> list = prodetailService.queryProSalesStatisticsByRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(list);
    }
}
