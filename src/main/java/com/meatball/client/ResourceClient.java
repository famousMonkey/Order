package com.meatball.client;

import com.meatball.common.client.BaseClient;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.vo.basicsResource.CommodityVo;
import com.meatball.common.vo.basicsResource.OilsVo;
import com.meatball.common.vo.classes.OilGunDutyVo;
import com.meatball.common.vo.classes.StaffDutyVo;
import com.meatball.common.vo.member.MemberDetailsVo;
import com.meatball.common.vo.oilstationvo.SafePassVo;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.RefundMutualInfo;
import com.meatball.common.vo.payinfo.customize.RefundVO;
import com.meatball.vo.BlanketorderVo;
import com.meatball.vo.OilSaleRetreatInfoVo;
import com.meatball.vo.OilStationVO;
import com.meatball.vo.discount.DiscountDataVo;
import com.meatball.vo.discount.DiscountProVo;
import com.meatball.vo.report.OilsorderReport;
import com.meatball.vo.report.ProductorderReport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Component
@FeignClient("mini-dolphin-gateway-zuul-server")
public interface ResourceClient extends BaseClient {

    //调用油站服务(通过油站Id获取油站编码)
    @GetMapping("/api/oilstationservice/oilstation/{resourceId}")
    ResultMsg<OilStationVO> getOilstationById(@PathVariable("resourceId") String resourceId);

    //通过油站编号获取订单编号
    @GetMapping("/api/oilStationNumber/createOrderNumber/{oilStationNumber}")
    String createOrderNumber (@PathVariable(value = "oilStationNumber") String oilStationNumber);

    @GetMapping(value = "/api/memberservice/memberinfo/details/{resourceId}")
    ResultMsg<MemberDetailsVo> queryMemberById (@PathVariable("resourceId") String resourceId);

    //油品进销存
    @PostMapping(value = "/api/invoicingservice/oiloutjoinlibraryinfo/saveandupdateresourceinfo")
    ResultMsg saveAndUpdateResourceInfo (@RequestBody OilSaleRetreatInfoVo resourceInfo);

    // 进销存，订单提交通知
    @PostMapping(value = "/api/invoicingservice/commodityadvance/orderpostnotice")
    ResultMsg<Object> orderPostNotice(@RequestBody OrderMutualInfo orderMutualInfo);

    // 进销存，订单完成通知
    @PostMapping(value = "/api/invoicingservice/commodityadvance/ordercompletenotice")
    ResultMsg<Object> orderCompleteNotice(@RequestBody OrderMutualInfo orderMutualInfo);

    // 进销存，订单取消通知
    @PostMapping(value = "/api/invoicingservice/commodityadvance/ordercancelnotice")
    ResultMsg<Object> orderCancelNotice(@RequestBody OrderMutualInfo orderMutualInfo);

    // 进销存，订单退款通知
    @PostMapping(value = "/api/invoicingservice/commodityadvance/orderretreatnotice")
    ResultMsg<Object> orderRetreatNotice(@RequestBody RefundMutualInfo refundMutualInfo);

    //根据员工ID获取员工执勤信息
    @GetMapping(value= "/api/classesservice/staffduty/fulla/bystaffid/{staffId}")
    ResultMsg<StaffDutyVo> getStaffDutyByStaffId(@PathVariable("staffId") String staffId);

    //根据油枪ID获取油枪执勤执勤信息
    @GetMapping(value= "/api/classesservice/oilgunduty/byoilgunid/{oilGunId}")
    ResultMsg<OilGunDutyVo> getOilgunDutyByOilGunId(@PathVariable("oilGunId") String oilGunId);

    //通知会员订单完成
    @PostMapping(value = "/api/memberservice/memberinfo/ordercomplete")
    ResultMsg<Object> memberOrderComplete(@RequestBody OrderMutualInfo orderMutualInfo);

    //根据油品id获取油品信息
    @GetMapping(value = "/api/basicsresourceservice/oils/{resourceId}")
    ResultMsg<OilsVo> selectOilsById(@PathVariable("resourceId") String resourceId);

    //根据油站ID获取油品列表信息
    @GetMapping(value = "/api/basicsresourceservice/oils/list")
    PageResultMsg<List<OilsVo>> selectOilsList(@RequestParam("oilStationId") String oilStationId,@RequestParam("Id") String Id);

    //根据商品id获取商品信息
    @GetMapping(value = "/api/basicsresourceservice/commodity/{resourceId}")
    ResultMsg<CommodityVo> selectCommodityById(@PathVariable("resourceId") String resourceId);

     // 验证安全密码
    @PostMapping("/api/oilstationservice/staff/verify/safepass")
    ResultMsg verifySafePass (@RequestBody SafePassVo safePassVo);

    //退款
    @PostMapping("/api/payservice/refund")
    ResultMsg payserviceRefund (@RequestBody RefundVO refund);

    //添加积分
    @PostMapping("/api/integralservice/integralprovide/ordercomplete")
    ResultMsg integralprovideOrdercomplete (@RequestBody OrderMutualInfo orderMutualInfo);

    //退积分
    @PostMapping("/api/integralservice/integralprovide/orderrefund")
    ResultMsg integralprovideOrderrefund (@RequestBody RefundMutualInfo refundMutualInfo);

    //添加优惠统计
    @PostMapping("/api/favorablestatisticsservice/orderdiscountinfo/notice")
    ResultMsg discountInfoOrdercomplete (@RequestBody OrderMutualInfo orderMutualInfo);

    //退优惠统计
    @PostMapping("/api/favorablestatisticsservice/orderdiscountinfo/refundnotice")
    ResultMsg discountInfoOrderrefund (@RequestBody RefundMutualInfo refundMutualInfo);

    //占用卡券
    @PutMapping("/api/privilege/usercouponservice/occupy/{resourceId}")
    ResultMsg userCouponOccupy(@PathVariable("resourceId") String resourceId,@RequestParam("modifier") String modifier,@RequestParam("oilStationId") String oilStationId);

    //撤销占用卡券
    @PutMapping("/api/privilege/usercouponservice/cancel/{resourceId}")
    ResultMsg userCouponCancel(@PathVariable("resourceId") String resourceId,@RequestParam("modifier") String modifier,@RequestParam("oilStationId") String oilStationId);

    //消费完成通知营销
    @PostMapping("/api/privilege/discountService/discount/consumeCompleteHandleNotice")
    ResultMsg consumeCompleteHandleNotice(@RequestBody OrderMutualInfo orderMutualInfo);

    //退款完成通知
    @PostMapping("/api/privilege/discountService/discount/refundCompleteHandleNotice")
    ResultMsg refundCompleteHandleNotice(@RequestBody RefundMutualInfo refundMutualInfo);

    //获取油品优惠
    @GetMapping("/api/privilege/discountService/discount/oil")
    ResultMsg<DiscountDataVo> discountOil(@RequestParam("condition") Map condition);

    //获取商品优惠
    @GetMapping("/api/privilege/goodsPromotionService/goodsPromotion/getGoodsDiscount")
    ResultMsg<DiscountProVo> discountPro(@RequestParam("condition") Map condition);

    //获取报表中油品订单列表
    @GetMapping("/api/report/order/oils/list")
    PageResultMsg<List<OilsorderReport>> oilsOrderList(@RequestParam("condition") Map condition);

    //获取报表中油品订单列表
    @GetMapping("/api/report/order/oils/page")
    PageResultMsg<List<OilsorderReport>> oilsOrderPage(@RequestParam("condition") Map condition);

    //获取报表中商品订单列表
    @GetMapping("/api/report/order/productprder/list")
    PageResultMsg<List<ProductorderReport>> productprderList(@RequestParam("condition") Map condition);

    //获取报表中商品订单列表
    @GetMapping("/api/report/order/productprder/page")
    PageResultMsg<List<ProductorderReport>> productprderPage(@RequestParam("condition") Map condition);

    //获取报表中总订单列表
    @GetMapping("/api/report/order/blanketorder/list")
    PageResultMsg<List<BlanketorderVo>> blanketOrderList(@RequestParam("condition") Map condition);

    //获取报表中总订单列表
    @GetMapping("/api/report/order/blanketorder/page")
    PageResultMsg<List<BlanketorderVo>> blanketOrderPage(@RequestParam("condition") Map condition);

    @PostMapping("/api/payservice/block")
    String blockTest(@RequestParam("milliSeconds") Long milliSeconds);

    @GetMapping("/api/classesservice/squadduty/fulla/list")
    PageResultMsg<List<Map>> squadDuty(@RequestParam("condition") Map condition);

    //油机数据解锁
    @PutMapping("/api/report/dispenser/lock-or-unlock/{code}")
    ResultMsg dispenserUnlock(@PathVariable("code") String code,@RequestBody Map map);

    //油机数据释放
    @DeleteMapping("/api/report/dispenser/delete/{code}")
    ResultMsg dispenserDelete(@PathVariable("code") String code);
}
