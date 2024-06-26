package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import com.sky.service.WorkspaceService;

import javax.swing.text.TabExpander;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = calculateDates(begin,end);
        String dateList = StringUtils.join(dates, ",");
        List<Double> turnOverlist = new ArrayList<>();
        for (LocalDate date : dates) {
            //营业额，状态为已完成的订单金额
            LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);//创建0.00分一天的开始
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);//创建23.59分一天的结束
//select sum(amount) from orders where order_time > dayBegin and orderTime < dayEnd and status = 5

            Map map= new HashMap();
            map.put("begin",dayBegin);
            map.put("end",dayEnd);
            map.put("status", Orders.COMPLETED);
            Double turnOver = orderMapper.sumByMap(map);
            turnOver = turnOver == null ? 0.0 : turnOver;
            turnOverlist.add(turnOver);
        }
        return TurnoverReportVO.builder().dateList(dateList).turnoverList(StringUtils.join(turnOverlist,",")).build();
    }


    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDates = calculateDates(begin, end);

        List<Integer> newUsers = new ArrayList<>();
        List<Integer> totalUsers = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            LocalDateTime dayBegin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end", dayEnd);
            Integer totalUser = userMapper.countbyMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUsers.add(totalUser);
            map.put("begin", dayBegin);
            Integer newUser = userMapper.countbyMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUsers.add(newUser);
//            Set<Map.Entry<LocalDate,LocalDate>> set = map.entrySet();
//            for (Map.Entry<LocalDate,LocalDate> entry : set) {
//                entry.getKey();
//                entry.getValue();
//            }
//        }
        }
        String dateList = StringUtils.join(localDates, ",");

        return UserReportVO.builder().
                dateList(dateList).
                newUserList(StringUtils.join(newUsers, ",")).
                totalUserList(StringUtils.join(totalUsers, ",")).build();
    }


    public List<LocalDate> calculateDates(LocalDate begin, LocalDate end){
        ArrayList<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while(!begin.equals(end)){
            //日期计算加入
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        return dates;
    }


    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDates = calculateDates(begin, end);
        List<Integer> totalOrders = new ArrayList<>();
        List<Integer> validOrders = new ArrayList<>();
        Map map = new HashMap<>();
        for (LocalDate localDate : localDates) {
            LocalDateTime dayBegin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(localDate, LocalTime.MAX);
            map.put("begin",dayBegin);
            map.put("end",dayEnd);
            Integer totalOrder = orderMapper.countAll(map);
            totalOrder = totalOrder == null ? 0:totalOrder;
            totalOrders.add(totalOrder);
            map.put("status",Orders.COMPLETED);
            Integer totalValidOrder = orderMapper.countAll(map);
            totalValidOrder = totalValidOrder == null ? 0 : totalValidOrder;
            validOrders.add(totalValidOrder);
        }
//        Integer total = totalOrders.stream().mapToInt(Integer::intValue).sum();
        Integer total = totalOrders.stream().reduce(Integer::sum).get();
        Integer totalValid = validOrders.stream().mapToInt(Integer::intValue).sum();
        Double percent =0.0;
        if(total != 0) percent = totalValid.doubleValue()/total.doubleValue();
        return OrderReportVO.builder().
                dateList(StringUtils.join(localDates,","))
                .orderCompletionRate(percent)
                .totalOrderCount(total)
                .validOrderCount(totalValid)
                .validOrderCountList(StringUtils.join(validOrders,","))
                .orderCountList(StringUtils.join(totalOrders,",")).build();
    }


    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //select sum(nums) as total,dishId from orderDetails group by dishId ORDER BY total desc limit 0,10
        LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime stop = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = orderDetailMapper.getSalesTop10(start,stop);
        StringJoiner stringJoiner = new StringJoiner(",");
        StringJoiner stringJoiner2 = new StringJoiner(",");
        for (GoodsSalesDTO goodsSalesDTO : top10) {
            stringJoiner.add(goodsSalesDTO.getName());
            Integer number = goodsSalesDTO.getNumber() == null ?0: goodsSalesDTO.getNumber();
            stringJoiner2.add(number.toString());
        }
        String nums = stringJoiner2.toString();
        String names = stringJoiner.toString();
        return SalesTop10ReportVO.builder().nameList(names).numberList(nums).build();
    }


    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/test.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
