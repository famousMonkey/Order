package com.meatball.controller;


import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseController;
import com.meatball.common.base.PageEntity;
import com.meatball.common.base.PageInfo;
import com.meatball.common.constant.MeatballConst;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.CopyUtils;
import com.meatball.common.utils.DateUtil;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.vo.member.MemberDetailsVo;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.ReceiptStatistics;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.common.vo.payinfo.customize.PayNotifyVo;
import com.meatball.entity.*;
import com.meatball.service.BlanketService;
import com.meatball.service.GoodsService;
import com.meatball.service.OilService;
import com.meatball.service.PorderService;
import com.meatball.vo.BlanketorderDetailsVo;
import com.meatball.vo.BlanketorderVo;
import com.meatball.vo.request.BlanketorderRe;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName:BlanketController
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 14:29
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/orderservice")
@Api(tags = "总订单信息")
public class BlanketController extends BaseController {

    @Autowired
    private BlanketService blanketService;

    @Autowired
    private OilService oilService;

    @Autowired
    private PorderService porderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ResourceClient resourceClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /***
     *根据id查询单个
     * @param: [blanketId]
     * @return: com.meatball.core.response.ResultMsg<Blanketorder>
     * @author: 周晓瀚
     * @date: 2018/11/5 16:46
     */
    @GetMapping("/blanketorder/{resourceId}")
    @ApiOperation(value = "根据id查询单个")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功)", response = BlanketorderVo.class)})
    public ResultMsg<BlanketorderVo> getBlanketorderById(@PathVariable("resourceId") String resourceId) {
        Blanketorder blanketorder;
        try {
            blanketorder = blanketService.selectResourceInfo(resourceId);

        } catch (Exception e) {
            return super.getErrorCode("操作失败");
        }
        if(blanketorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(BlanketorderVo.class,blanketorder));
    }
    /***
     *
     * @param: [blanketorderNo]
     * @return: com.meatball.common.response.ResultMsg<com.meatball.entity.Blanketorder>
     * @author: 周晓瀚
     * @date: 2018/12/5 15:43 
     */
    @GetMapping("/blanketorder/blanketorderno/{blanketorderNo}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功)", response = BlanketorderVo.class)})
    @ApiOperation(value = "根据订单编号查询信息")
    public ResultMsg<BlanketorderVo> getBlanketorderByblanketorderNo(@PathVariable("blanketorderNo") String blanketorderNo) {
        Blanketorder blanketorder = blanketService.getBlanketorderByblanketorderNo(blanketorderNo);
        if(blanketorder == null){
            return super.getErrorCode("查询不到结果");
        }
        return super.getSuccessCode(CopyUtils.copyObject(BlanketorderVo.class,blanketorder));
    }

    /***
     *分页查询所有
     * @param: [pageNum, pageSize, dutyStatus]
     * @return: com.meatball.core.response.PageResultMsg<org.springframework.data.domain.Page<com.meatball.entity.Duty>>
     * @author: 周晓瀚
     * @date: 2018/11/3  9:48
     */
    @GetMapping(value= "/blanketorder/page")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = BlanketorderVo.class)})
    @ApiOperation(value = "分页查询信息")
    public PageResultMsg queryPage (PageEntity pageEntity, BlanketorderRe blanketorder){
        Map query= MapObjUtil.object2Map(blanketorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Blanketorder> page = blanketService.queryPage(query);
        PageInfo<BlanketorderVo> pageInfo = new PageInfo<>(CopyUtils.copyArray(BlanketorderVo.class,page.getContent()));
        pageInfo.setTotalElements(page.getTotalElements());
        return super.getSuccessCode(pageInfo);
    }
    /***
     *查询列表
     * @param: [query]
     * @return: PageResultMsg
     * @author: 周晓瀚
     * @date: 2018/11/7 16:27
     */
    @ApiOperation(value="获取列表信息", notes="获取列表信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = BlanketorderVo.class)})
    @GetMapping(value = "/blanketorder/list")
    public PageResultMsg queryList (BlanketorderRe blanketorder){
        Map query= MapObjUtil.object2Map(blanketorder);
        List<Blanketorder> list = blanketService.queryList(query);
        return super.getSuccessCode(CopyUtils.copyArray(BlanketorderVo.class,list));
    }


    @GetMapping(value= "/blanketorder/report/page")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = BlanketorderVo.class)})
    @ApiOperation(value = "获取报表分页列表")
    public PageResultMsg queryReportPage (PageEntity pageEntity,BlanketorderRe blanketorder ){
        Map query= MapObjUtil.object2Map(blanketorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        return resourceClient.blanketOrderPage(query);
    }

    @ApiOperation(value="获取报表列表", notes="获取列表信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = BlanketorderVo.class)})
    @GetMapping(value = "/blanketorder/report/list")
    public PageResultMsg queryReportList (BlanketorderRe blanketorder){
        Map query = MapObjUtil.object2Map(blanketorder);
        return resourceClient.blanketOrderList(query);
    }

    /***
     * 查询订单交互信息
     * @author: 张垒
     * @date: 2019/2/13
     */
    @ApiOperation(value="通过订单号获取订单交互信息", notes="订单交互信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = OrderMutualInfo.class)})
    @GetMapping(value = "/blanketorder/orderMutualByOrderNo/{orderNo}")
    public ResultMsg queryOrderMutualByOrderNo(@PathVariable("orderNo") String orderNo){
        OrderMutualInfo orderMutualInfo=blanketService.selectOrderMutual(orderNo);
        return super.getSuccessCode(orderMutualInfo);
    }

    /***
     * 查询订单交互信息
     * @author: 张垒
     * @date: 2019/2/19
     */
    @ApiOperation(value="通过会员ID获取挂单交互信息", notes="订单交互信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = OrderMutualInfo.class)})
    @GetMapping(value = "/blanketorder/orderMutualByMemberId/{memberId}")
    public ResultMsg queryOrderMutualByMemberId(@PathVariable("memberId") String memberId,String oilStationId){
        if(StringUtils.isBlank(oilStationId)){
            return super.getErrorCode("油站ID不能为空");
        }
        List<Blanketorder> list = blanketService.queryUndoneByMemberId(oilStationId,memberId);
        OrderMutualInfo orderMutualInfo = null;
        if(list!=null && list.size() > 0){
            orderMutualInfo=blanketService.selectOrderMutual(list.get(0).getBlanketorderNo());
        }
        return super.getSuccessCode(orderMutualInfo);
    }


    /***
     * 订单交互-标准版
     * @author: 张垒
     * @date: 2019/2/13
     */
    @PostMapping(value = "/blanketorder/ordermutual")
    @ApiOperation(value = "订单交互-标准版")
    @TxcTransaction
    @Transactional
    public ResultMsg orderMutual(@RequestBody OrderMutualInfo orderMutualInfo){
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 提交订单 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println("接收数据："+JSON.toJSONString(orderMutualInfo));

        String key = "orderMutual"+ orderMutualInfo.getMemberId();
        if(orderMutualInfo.getInterim()){
            key = "orderMutual"+ orderMutualInfo.toString();
        }

        ResultMsg  resultMsg = super.getErrorCode("订单提交失败");

        if (orderMutualInfo == null || (orderMutualInfo.getOilOrder() == null && orderMutualInfo.getProductOrders()==null)){

            resultMsg = super.getCustomizeCode(21011,"提交信息错误");

        }else if(StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(key))){

            resultMsg =  super.getCustomizeCode(21009,"提交订单中，请稍候操作");

        } else {

            stringRedisTemplate.opsForValue().set(key, "0", 10, TimeUnit.SECONDS);

            int i = blanketService.orderMutual(orderMutualInfo);
            if (i == 0) {
                resultMsg = super.getSuccessCode(blanketService.selectOrderMutual(orderMutualInfo.getBlanketorderNo()));
            } else {
                switch (i) {
                    case 1:
                        resultMsg = super.getCustomizeCode(21001, "存在未完成订单", orderMutualInfo);
                        break;
                    case 2:
                        resultMsg = super.getCustomizeCode(21002, "单价错误，请重新提交订单", orderMutualInfo);
                        break;
                    case 3:
                        resultMsg = super.getCustomizeCode(21003, "优惠错误，请重新提交订单", orderMutualInfo);
                        break;
                    case 4:
                        resultMsg = super.getCustomizeCode(21004, "计算错误", orderMutualInfo);
                        break;
                    case 5:
                        resultMsg = super.getCustomizeCode(21005, "状态错误", orderMutualInfo);
                        break;
                    case 6:
                        resultMsg = super.getCustomizeCode(21006, "无执勤信息", orderMutualInfo);
                        break;
                    case 7:
                        resultMsg = super.getCustomizeCode(21007, "订单不允许修改", orderMutualInfo);
                        break;
                    case 8:
                        resultMsg = super.getCustomizeCode(21008, "订单编号错误", orderMutualInfo);
                        break;
                    case 9:
                        resultMsg = super.getCustomizeCode(21009, "会员信息错误", orderMutualInfo);
                        break;
                    case 10:
                        resultMsg = super.getCustomizeCode(21010, "油枪错误", orderMutualInfo);
                        break;
                    case 11:
                        resultMsg = super.getCustomizeCode(21011, "提交信息错误", orderMutualInfo);
                        break;
                    case 12:
                        resultMsg = super.getCustomizeCode(21012, "获取优惠失败", orderMutualInfo);
                        break;
                    case 13:
                        resultMsg = super.getCustomizeCode(21012, "库存不足，请重新提交订单", orderMutualInfo);
                        break;
                }
            }
        }

        stringRedisTemplate.delete(key);
        System.out.println("响应数据："+JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 提交订单 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return resultMsg;

    }

    /***
     *
     * @param: [payNotifyVo]
     * @return: com.meatball.common.response.ResultMsg
     * @author: 周晓瀚
     * @date: 2018/11/28 17:50
     */
    @PutMapping(value = "/blanketorder/paymentnotice")
    @ApiOperation(value = "订单支付完成支付通知")
    @TxcTransaction
    @Transactional
    public ResultMsg orderPaymentNotice(@RequestBody PayNotifyVo payNotifyVo) {

        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 支付通知 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println("接收数据："+JSON.toJSONString(payNotifyVo));

        Blanketorder blanketorder =  blanketService.selectResourceInfo(payNotifyVo.getRelevantId());

        ResultMsg resultMsg;

        if (blanketorder == null || blanketorder.getBlankSta()!= MeatballConst.ONE) {
            resultMsg = super.getErrorCode("订单错误");
        }else {
            super.packageUpdateProperty(payNotifyVo);
            Map map =  blanketService.orderPaymentNotice(payNotifyVo);
            resultMsg = super.getSuccessCode(map);
        }

        System.out.println("响应数据："+JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 支付通知 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return resultMsg;
    }

    /***
     *
     * @param: [orderCancellationVo]
     * @return: com.meatball.common.response.ResultMsg
     * @author: 周晓瀚
     * @date: 2018/12/3 16:15
     */
    @PutMapping(value = "/blanketorder/ordercancellation")
    @ApiOperation(value = "订单取消")
    @TxcTransaction
    @Transactional
    public  ResultMsg orderCancellation(@RequestBody OrderCancellation orderCancellation){
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 订单取消 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ResultMsg resultMsg;
        String key = "orderCancellation"+ orderCancellation.getBlanketorderId();
        if(StringUtils.isNotBlank(stringRedisTemplate.opsForValue().get(key))){
            resultMsg = super.getCustomizeCode(21009,"订单取消中，请稍候操作");
        }else {
            stringRedisTemplate.opsForValue().set(key, "0", 10, TimeUnit.SECONDS);
            Blanketorder blanketorder = blanketService.selectResourceInfo(orderCancellation.getBlanketorderId());

            if(blanketorder == null){
                resultMsg = super.getErrorCode("未找到订单");
            }else  if (blanketorder.getBlankSta()!=0 && blanketorder.getBlankSta()!=1){
                resultMsg =  super.getErrorCode("无法取消");
            }else {
                super.packageInsertProperty(orderCancellation);
                blanketService.orderCancellation(orderCancellation);
                resultMsg = super.getSuccessCode();
            }
            stringRedisTemplate.delete(key);
        }
        System.out.println("响应数据："+JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 订单取消 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return resultMsg;
    }

    @PutMapping(value = "/blanketorder/orderinvoice/{blanketorderId}")
    @ApiOperation(value = "开发票标记")
    public ResultMsg orderCancellation(@PathVariable("blanketorderId") String blanketorderId){
        Blanketorder blanketorder = blanketService.selectResourceInfo(blanketorderId);
        if(blanketorder == null){
            return super.getErrorCode("未找到订单");
        }
        if (blanketorder.getInvoiceSta()!=null && blanketorder.getInvoiceSta() == 1){
            return super.getErrorCode("已开发票");
        }
        if (blanketorder.getBlankSta()!=2 || blanketorder.getRefundSta() != 0 ){
            return super.getErrorCode("不允许开发票");
        }

        super.packageInsertProperty(blanketorder);
        blanketorder.setInvoiceSta(1);
        blanketService.updateResourceInfo(blanketorder);
        return super.getSuccessCode(CopyUtils.copyObject(BlanketorderVo.class,blanketorder));
    }

    /***
     *
     * @param: [pageEntity, blanketorder]
     * @return: com.meatball.common.response.PageResultMsg
     * @author: 周晓瀚
     * @date: 2018/11/28 11:17
     */
    @GetMapping(value= "/blanketorder/detailspage")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = BlanketorderDetailsVo.class)})
    @ApiOperation(value = "分页查询详情信息")
    public PageResultMsg queryDetailsPage (PageEntity pageEntity,BlanketorderRe blanketorder){
        Map query= MapObjUtil.object2Map(blanketorder);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Blanketorder> page = blanketService.queryPage(query);
        PageInfo<BlanketorderDetailsVo> pageInfo = null;
        if(page != null){
            List blanketorderDetailses = new ArrayList<>();
            for (Blanketorder entity:page) {
                BlanketorderDetailsVo blanketorderDetails=new BlanketorderDetailsVo();
                BeanUtils.copyProperties(entity,blanketorderDetails);
                blanketorderDetails.setOilsorder(oilService.selectOilorderByBlanketId(entity.getId()));
                blanketorderDetails.setProductorder(porderService.selectProductByblanketOrderId(entity.getId()));
                blanketorderDetailses.add(blanketorderDetails);
            }
            pageInfo=new PageInfo<BlanketorderDetailsVo>(blanketorderDetailses);
            pageInfo.setTotalElements(page.getTotalElements());
        }

        return super.getSuccessCode(pageInfo);
    }

    @GetMapping(value= "/blanketorder/memberconsumption/{memberId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = SalesStatistics.class)})
    @ApiOperation(value = "查询会员消费信息")
    public ResultMsg queryMemberConsumption (@PathVariable("memberId") String memberId,
                                             String startDate,String endDate){

        if(StringUtils.isBlank(memberId)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        if(StringUtils.isNotBlank(startDate) && !DateUtil.isDateString(startDate)){
            return super.getCustomizeCode(10031,"查询失败");
        }
        if(StringUtils.isNotBlank(endDate) && !DateUtil.isDateString(endDate)){
            return super.getCustomizeCode(10031,"查询失败");
        }

        SalesStatistics salesStatistics = blanketService.queryMemberConsumption(memberId,startDate,endDate);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/blanketorder/oilstationconsumption/{relatedId}")
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
        SalesStatistics salesStatistics = blanketService.queryConsumptionByOilRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(salesStatistics);
    }

    @GetMapping(value= "/blanketorder/oilstationreceiptstatistics/{relatedId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = ReceiptStatistics.class)})
    @ApiOperation(value = "查询油站收款统计")
    public ResultMsg queryOilStationReceiptStatistics (@PathVariable("relatedId") String relatedId,
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

        ReceiptStatistics receiptStatistics = blanketService.queryReceiptStatisticsByRelatedId(relatedId,startDate,endDate);
        return super.getSuccessCode(receiptStatistics);
    }

    @GetMapping(value= "/blanketorder/reportDataImport/{oilStationId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功")})
    @ApiOperation(value = "报表数据导入")
    public ResultMsg reportDataImport (@PathVariable("oilStationId") String oilStationId){

        BlanketorderRe blanketorder = new BlanketorderRe();
        blanketorder.setOilStationId(oilStationId);
        Map query = MapObjUtil.object2Map(blanketorder);
        List<Blanketorder> list = blanketService.queryList(query);

        List<BlanketorderVo> reportList;
        PageResultMsg<List<BlanketorderVo>> resultMsg = resourceClient.blanketOrderList(query);
        if(resultMsg.getCode() != 10000){
            return super.getCustomizeCode(10001,"报表查询："+resultMsg.getMessage());
        }
        reportList = resultMsg.getData();
        Map<String ,Object> result = new LinkedHashMap<>();
        result.put("OrderQuantity",list.size());
        result.put("ReportQuantity",reportList.size());

        Integer insertQuantity = 0;
        Integer updateQuantity = 0;
        Integer failureQuantity = 0;

        for (Blanketorder order : list){
            List<BlanketorderVo> tempList = reportList.stream() .filter(u -> u.getId().equals(order.getId())).collect(Collectors.toList());
            if(tempList.size()<=0 || !tempList.get(0).getBlankSta().equals(order.getBlankSta())){

                Boolean failure = false;
                Map<String,String> rabbitInfo = new HashMap<>();
                rabbitInfo.put("blanketOrder",JSON.toJSONString(order));

                Oilsorder oilsorder = oilService.selectOilorderByBlanketId(order.getId());
                if(oilsorder!=null){
                    rabbitInfo.put("oilsOrder",JSON.toJSONString(oilsorder));
                }

                Productorder productorder = porderService.selectProductByblanketOrderId(order.getId());
                if(productorder!=null){
                    rabbitInfo.put("productOrder",JSON.toJSONString(productorder));
                }

                if(tempList.size()>0){
                    updateQuantity++;
                }else {
                    if(StringUtils.isNotBlank(order.getMemberId())){
                        ResultMsg<MemberDetailsVo> member = resourceClient.queryMemberById(order.getMemberId());
                        if(member.getCode() != 10000 || member.getData() == null){
                            failureQuantity++;
                            failure = true;
                        }
                    }
                    insertQuantity++;
                }

                if(!failure){
                    if(order.getRefundSta() != 0){
                        List<GoodsRefund> goodsRefunds = goodsService.selectGoodsRefundsByBlanketOrderId(order.getId());
                        for (GoodsRefund goodsRefund:goodsRefunds){
                            rabbitInfo.put("refundOrder",JSON.toJSONString(goodsRefund));
                            rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
                        }
                    }else {
                        rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
                    }

                }
            }
        }

        result.put("InsertQuantity",insertQuantity);
        result.put("UpdateQuantity",updateQuantity);
        result.put("FailureQuantity",failureQuantity);

        return super.getSuccessCode(result);
    }

    @GetMapping(value= "/blanketorder/reportDataComparison/{oilStationId}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功")})
    @ApiOperation(value = "报表数据比对")
    public ResultMsg reportDataComparison (@PathVariable("oilStationId") String oilStationId){
        BlanketorderRe blanketorder = new BlanketorderRe();
        blanketorder.setOilStationId(oilStationId);
        Map query = MapObjUtil.object2Map(blanketorder);
        List<Blanketorder> list = blanketService.queryList(query);

        Map<String ,Object> result = new LinkedHashMap<>();

        List<BlanketorderVo> reportList;
        PageResultMsg<List<BlanketorderVo>> resultMsg = resourceClient.blanketOrderList(query);
        if(resultMsg.getCode() != 10000){
            return super.getCustomizeCode(10001,"报表查询："+resultMsg.getMessage());
        }
        reportList = resultMsg.getData();
        result.put("OrderQuantity",list.size());
        result.put("ReportQuantity",reportList.size());

        List<BlanketorderVo> insertData = new ArrayList<>();
        List<BlanketorderVo> updateData = new ArrayList<>();

        for (Blanketorder order : list){
            List<BlanketorderVo> tempList = reportList.stream() .filter(u -> u.getId().equals(order.getId())).collect(Collectors.toList());
            if(tempList.size()>0){
                if(!tempList.get(0).getBlankSta().equals(order.getBlankSta())){
                    updateData.add(CopyUtils.copyObject(BlanketorderVo.class,order));
                }
            }else {
                insertData.add(CopyUtils.copyObject(BlanketorderVo.class,order));
            }
        }

        result.put("InsertQuantity",insertData.size());
        result.put("UpdateQuantity",updateData.size());
        result.put("InsertData",insertData);
        result.put("UpdateData",updateData);
        return super.getSuccessCode(result);
    }


}
