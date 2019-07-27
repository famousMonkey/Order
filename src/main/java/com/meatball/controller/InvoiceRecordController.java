package com.meatball.controller;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.common.base.BaseController;
import com.meatball.common.base.PageEntity;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.InvoiceRecord;
import com.meatball.service.BlanketService;
import com.meatball.service.InvoiceRecordService;
import com.meatball.vo.InvoiceCancelVo;
import com.meatball.vo.request.InvoiceRecordRe;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderservice")
@Api(tags = "发票相关")
public class InvoiceRecordController extends BaseController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BlanketService blanketService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @PostMapping(value = "/invoicerecord/issue")
    @ApiOperation(value = "提交开发票")
    @TxcTransaction
    @Transactional
    public ResultMsg invoiceIssue(@RequestBody InvoiceRecord invoiceRecord){
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 开具发票 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println("接收数据："+ JSON.toJSONString(invoiceRecord));

        if(StringUtils.isBlank(invoiceRecord.getOrderId())){
            super.getCustomizeCode(21001,"订单ID不能为空！");
        }

        ResultMsg  resultMsg = super.getSuccessCode();

        Blanketorder order = blanketService.selectResourceInfo(invoiceRecord.getOrderId());

        if(order == null ){
            resultMsg =  super.getCustomizeCode(21002,"未查到订单！");
        }else if(order.getInvoiceSta().equals(1)){
            resultMsg = super.getCustomizeCode(21003,"已开发票！");
        }else if(!invoiceRecordService.invoiceIssue(invoiceRecord,order)){
            resultMsg = super.getCustomizeCode(21010,"提交开票失败！");
        }

        System.out.println("响应数据："+JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 支付通知 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return  resultMsg ;
    }

    @PostMapping(value = "/invoicerecord/cancel")
    @ApiOperation(value = "撤销发票")
    @TxcTransaction
    @Transactional
    public ResultMsg invoiceCancel(@RequestBody InvoiceCancelVo invoiceCancel){
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 撤销发票 任务开始 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        System.out.println("接收数据："+ JSON.toJSONString(invoiceCancel));
        if(StringUtils.isBlank(invoiceCancel.getOrderId())){
            super.getCustomizeCode(21001,"订单ID不能为空！");
        }
        ResultMsg  resultMsg = super.getSuccessCode();
        InvoiceRecord invoiceRecord = invoiceRecordService.queryByOrderId(invoiceCancel.getOrderId());
        if(invoiceRecord == null){
            resultMsg =  super.getCustomizeCode(21002,"未查到开票信息！");
        }else if(invoiceRecord.getStatus().equals(1)){
            resultMsg =  super.getCustomizeCode(21002,"已撤销发票，不允许再次撤销！");
        }else if(!invoiceRecordService.invoiceCancel(invoiceRecord,invoiceCancel)){
            resultMsg =  super.getCustomizeCode(21002,"撤销发票失败！");
        }

        System.out.println("响应数据："+JSON.toJSONString(resultMsg));
        System.out.println("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 撤销发票 任务结束 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        return  resultMsg ;
    }

    @GetMapping(value= "/invoicerecord/byinvoicenum/{invoiceNum}")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功", response = InvoiceRecord.class)})
    @ApiOperation(value = "根据发票流水号查询")
    public ResultMsg queryByInvoiceNum (@PathVariable("invoiceNum") String invoiceNum){

        InvoiceRecord invoiceRecord = invoiceRecordService.queryByInvoiceNum(invoiceNum) ;
        return super.getSuccessCode(invoiceRecord);
    }

    @GetMapping(value= "/invoicerecord/page")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = InvoiceRecord.class)})
    @ApiOperation(value = "分页查询信息")
    public PageResultMsg queryPage (PageEntity pageEntity, InvoiceRecordRe invoiceRecord){
        Map query= MapObjUtil.object2Map(invoiceRecord);
        query.putAll(MapObjUtil.object2Map(pageEntity));
        Page<Blanketorder> page = invoiceRecordService.queryPage(query);
        return super.getSuccessCode(page);
    }

    @ApiOperation(value="获取列表信息", notes="获取列表信息")
    @ApiResponses({@ApiResponse(code = 10000, message = "成功(返回结果为集合)", response = InvoiceRecord.class)})
    @GetMapping(value = "/invoicerecord/list")
    public PageResultMsg queryList ( InvoiceRecordRe invoiceRecord){
        Map query= MapObjUtil.object2Map(invoiceRecord);
        List<Blanketorder> list = blanketService.queryList(query);
        return super.getSuccessCode(list);
    }

}
