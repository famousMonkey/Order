package com.meatball.controller;

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
import com.meatball.common.vo.basicsResource.OilsVo;
import com.meatball.common.vo.ordervo.customize.OilSalesStatistics;
import com.meatball.common.vo.ordervo.customize.OrderStatistics;
import com.meatball.common.vo.ordervo.customize.ReceiptStatistics;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.entity.Oilsorder;
import com.meatball.service.OilService;
import com.meatball.vo.OilsorderVo;
import com.meatball.vo.report.OilsorderReport;
import com.meatball.vo.report.OilsorderReportRe;
import com.meatball.vo.request.OilsorderRe;
import com.meatball.vo.request.StatisticsCondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @ClassName:OilController
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 14:56
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/orderservice")
@Api(tags = "油品订单")
public class OilController extends BaseController {
    @Autowired
    private OilService oilService;
    @Autowired
    private ResourceClient resourceClient;

    @GetMapping("/oilorder/{resourceId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = Oilsorder.class)})
    @ApiOperation(value = "根据id查询单个")
    public ResultMsg getOilsorderById(@PathVariable("resourceId") String resourceId) {
        Oilsorder oilsorder= oilService.selectResourceInfo(resourceId);
        if(oilsorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(OilsorderVo.class,oilsorder));
    }

    @GetMapping(value= "/oilorder/page")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = OilsorderVo.class)})
    @ApiOperation(value = "查询信息分页列表")
    public PageResultMsg queryPage (PageEntity pageEntity,OilsorderRe oilsorder ){
        Map query= MapObjUtil.object2Map(oilsorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Oilsorder> page = oilService.queryPage(query);
        PageInfo<OilsorderVo> pageInfo = new PageInfo<>(CopyUtils.copyArray(OilsorderVo.class,page.getContent()));
        pageInfo.setTotalElements(page.getTotalElements());
        return super.getSuccessCode(pageInfo);
    }

    @ApiOperation(value="获取列表信息", notes="获取列表信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = OilsorderVo.class)})
    @GetMapping(value = "/oilorder/list")
    public PageResultMsg queryList (OilsorderRe oilsorder){
        Map query = MapObjUtil.object2Map(oilsorder);
        List<Oilsorder> list = oilService.queryList(query);
        return super.getSuccessCode(CopyUtils.copyArray(OilsorderVo.class,list));

    }

    @GetMapping(value= "/oilorder/report/page")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = OilsorderReport.class)})
    @ApiOperation(value = "获取报表分页列表")
    public PageResultMsg queryReportPage (PageEntity pageEntity,OilsorderReportRe oilsorder ){
        String[] refundSta=null;
        if( oilsorder.getRefundSta() != null &&  oilsorder.getRefundSta().split(",").length>1){
            refundSta = oilsorder.getRefundSta().split(",");
            oilsorder.setRefundSta(null);
        }
        Map query= MapObjUtil.object2Map(oilsorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));

        if(refundSta != null){
            for (int i = 0;i<refundSta.length; i++){
                query.put("refundSta["+i+"]",refundSta[i]);
            }
        }

        return resourceClient.oilsOrderPage(query);
    }

    @ApiOperation(value="获取报表列表", notes="获取列表信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = OilsorderReport.class)})
    @GetMapping(value = "/oilorder/report/list")
    public PageResultMsg queryReportList (OilsorderReportRe oilsorder){
        Map query = MapObjUtil.object2Map(oilsorder);
        return resourceClient.oilsOrderList(query);
    }

    @GetMapping("/oilorder/byblanketid/{blanketOrderId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = Oilsorder.class)})
    @ApiOperation(value = "根据总订单Id信息查询油品订单")
    public ResultMsg getOilorderByBlandetId(@PathVariable("blanketOrderId") String blanketOrderId) {
        Oilsorder oilsorder = oilService.selectOilorderByBlanketId(blanketOrderId);
        if(oilsorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(OilsorderVo.class,oilsorder));
    }

    @GetMapping("/oilorder/oilsorderno/{oilsorderNo}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = Oilsorder.class)})
    @ApiOperation(value = "根据订单编号查询油品订单")
    public ResultMsg getOilorderByoilsorderNo(@PathVariable("oilsorderNo") String oilsorderNo) {
        Oilsorder oilsorder = oilService.selectOilorderByoilsorderNo(oilsorderNo);
        if(oilsorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(OilsorderVo.class,oilsorder));
    }

    @GetMapping(value= "/oilorder/salesstatistics")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "油品订单列表统计")
    public ResultMsg querySalesStatistics (OilsorderRe oilsorder ){
        Map query = MapObjUtil.object2Map(oilsorder);
        SalesStatistics salesStatistics = oilService.queryListSalesStatistics(query);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/oilorder/orderstatisticsbystaffdutyId/{staffDutyId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = OrderStatistics.class)})
    @ApiOperation(value = "根据员工执勤ID获取订单统计")
    public ResultMsg queryOrderStatistics (@PathVariable("staffDutyId") String staffDutyId,String oilGunId){
        StatisticsCondition condition = new StatisticsCondition();
        condition.setStaffDutyId(staffDutyId);
        if(StringUtils.isNotBlank(oilGunId)){
            condition.setOilGunId(oilGunId);
        }
        OrderStatistics orderStatistics = oilService.queryOrderStatistics(condition);
        return super.getSuccessCode(orderStatistics);
    }

    @GetMapping(value= "/oilorder/orderstatisticsbyoilgunId/{oilGunId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功)", response = OrderStatistics.class)})
    @ApiOperation(value = "根据油枪ID获取订单统计")
    public ResultMsg queryOilOrderStatistics (@PathVariable("oilGunId") String oilGunId){
        StatisticsCondition condition = new StatisticsCondition();
        condition.setOilGunId(oilGunId);
        OrderStatistics orderStatistics = oilService.queryOrderStatistics(condition);
        return super.getSuccessCode(orderStatistics);
    }
    @GetMapping(value= "/oilorder/orderstatisticsbyoilsId/{oilsId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = OrderStatistics.class)})
    @ApiOperation(value = "根据油品ID获取订单统计")
    public ResultMsg queryOrderStatisticsByOilsId (@PathVariable("oilsId") String oilsId){
        StatisticsCondition condition = new StatisticsCondition();
        condition.setOilsId(oilsId);
        OrderStatistics orderStatistics = oilService.queryOrderStatistics(condition);
        return super.getSuccessCode(orderStatistics);
    }

    @GetMapping(value= "/oilorder/receiptstatisticsbydutyId/{dutyId}")
    @ApiOperation(value = "根据班组执勤ID获取收款方式统计")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = ReceiptStatistics.class)})
    public ResultMsg queryMemberReceiptStatistics (@PathVariable("dutyId") String dutyId,
                                                   String refuelStaffId,String oilGunId){
        StatisticsCondition condition = new StatisticsCondition();
        condition.setDutyId(dutyId);
        if(StringUtils.isNotBlank(refuelStaffId)){
            condition.setRefuelStaffId(refuelStaffId);
        }
        if(StringUtils.isNotBlank(oilGunId)){
            condition.setOilGunId(oilGunId);
        }
        ReceiptStatistics receiptStatistics = oilService.queryReceiptStatistics(condition);
        return super.getSuccessCode(receiptStatistics);
    }

    @GetMapping(value= "/oilorder/receiptstatisticsbystaffdutyId/{staffDutyId}")
    @ApiOperation(value = "根据员工执勤ID获取收款方式统计")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = ReceiptStatistics.class)})
    public ResultMsg queryMemberReceiptStatistics (@PathVariable("staffDutyId") String staffDutyId){

        StatisticsCondition condition = new StatisticsCondition();
        condition.setStaffDutyId(staffDutyId);
        ReceiptStatistics receiptStatistics = oilService.queryReceiptStatistics(condition);
        return super.getSuccessCode(receiptStatistics);
    }

    @GetMapping(value= "/oilorder/salesstatisticsbydutyid/{dutyId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "根据班组执勤ID查询销售统计")
    public ResultMsg querySalesStatistics (@PathVariable("dutyId") String dutyId,String oilGunId,String payment){

        StatisticsCondition condition = new StatisticsCondition();
        condition.setDutyId(dutyId);
        if(StringUtils.isNotBlank(oilGunId)){
            condition.setOilGunId(oilGunId);
        }
        if(StringUtils.isNotBlank(payment)){
            condition.setPayment(payment);
        }
        SalesStatistics salesStatistics = oilService.querySalesStatistics(condition);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/oilorder/salesstatisticsbystaffdutyid/{staffDutyId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "根据员工执勤ID查询销售统计")
    public ResultMsg querySalesStatisticsByStaffDutyId (@PathVariable("staffDutyId") String staffDutyId,String oilGunId,String payment){
        StatisticsCondition condition = new StatisticsCondition();
        condition.setStaffDutyId(staffDutyId);
        if(StringUtils.isNotBlank(oilGunId)){
            condition.setOilGunId(oilGunId);
        }
        if(StringUtils.isNotBlank(payment)){
            condition.setPayment(payment);
        }
        SalesStatistics salesStatistics = oilService.querySalesStatistics(condition);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/oilorder/oilsalesstatistics/{relatedId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = OilSalesStatistics.class)})
    @ApiOperation(value = "查询油品统计信息")
    public PageResultMsg queryOilsConsumption (@PathVariable("relatedId") String relatedId,
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
        List<OilSalesStatistics> list = oilService.queryOilSalesStatisticsByRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(list);
    }

    @GetMapping(value= "/oilorder/oilstationconsumption/{relatedId}")
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
        SalesStatistics salesStatistics = oilService.queryConsumptionByRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(salesStatistics);
    }

    @ApiOperation(value="油品订单导出")
    @GetMapping(value = "/oilorder/export")
    public void exportExcelOilorder(OilsorderReportRe oilsorder, HttpServletResponse response) {
        String tableName= DateUtil.getAllTime();
        String[] refundSta=null;
        if( oilsorder.getRefundSta() != null &&  oilsorder.getRefundSta().split(",").length>1){
            refundSta = oilsorder.getRefundSta().split(",");
            oilsorder.setRefundSta(null);
        }
        Map query= MapObjUtil.object2Map(oilsorder);

        if(refundSta != null){
            for (int i = 0;i<refundSta.length; i++){
                query.put("refundSta["+i+"]",refundSta[i]);
            }
        }
        List<OilsorderReport> list = oilService.queryOilsorderReportList(query);

        Map<String, String> header = new LinkedHashMap<>();
        header.put("OilsorderNo", "订单编号");
        header.put("OrderGenerationTime", "下单时间");
        header.put("MemberPhone", "手机号");
        header.put("TextPaymentMethod", "支付方式");
        header.put("CopeWith", "实付金额(元)");
        header.put("Discounts", "优惠金额(元)");
        header.put("OilMoney", "订单金额(元)");
        header.put("OilsName", "油品");
        header.put("OilGunName", "枪号");
        header.put("Quantity", "油量");
        header.put("Price", "单价(元/升)");
        header.put("TextOrderStatus", "状态");

        ExcelExport export = new ExcelExport(tableName);
        try {
            export.writeExcel(list,header,0,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value="油站销售汇总")
    @GetMapping(value = "/oilorder/salesreport/{oilStationId}")
    public void exportExcelDailyReport(@PathVariable("oilStationId") String oilStationId,String oilsId,
                                       String startDate,String endDate,HttpServletResponse response) {
        String tableName= DateUtil.getAllTime();

        List<OilsVo> oilsVos = new ArrayList<>();
        PageResultMsg<List<OilsVo>> resultMsg = resourceClient.selectOilsList(oilStationId,oilsId);
        if(resultMsg.getCode() == 10000){
            oilsVos = resultMsg.getData();
        }

        List<Map> list = oilService.queryDailyReport(oilStationId,startDate,endDate,oilsVos);
        ExcelExport export = new ExcelExport(tableName);
        Map<String, String> header = new LinkedHashMap<>();
        String title = "油站销售汇总("+startDate+" - "+endDate+"),";
        header.put("orderDate", title + "日期,日期");
        for (OilsVo oils : oilsVos){
            header.put(oils.getId(), title + "油品销量," + oils.getName());
        }
        header.put("cash", title + "支付方式实收金额（元）,现金");
        header.put("swipe", title + "支付方式实收金额（元）,银行卡");
        header.put("wechat", title + "支付方式实收金额（元）,微信");
        header.put("alipay", title + "支付方式实收金额（元）,支付宝");
        header.put("commonAccount", title + "支付方式实收金额（元）,通用账户");
        header.put("gasolineAccount", title + "支付方式实收金额（元）,汽油账户");
        header.put("dieselAccount", title + "支付方式实收金额（元）,柴油账户");
        header.put("cngAccount", title + "支付方式实收金额（元）,CNG账户");
        header.put("iou", title + "支付方式实收金额（元）,白条");
        header.put("total", title + "支付方式实收金额（元）,合计");
        export.putExcelShett("sheet1", list,header);

        try {
            export.writeExcel(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
