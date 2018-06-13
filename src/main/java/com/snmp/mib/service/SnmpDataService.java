package com.snmp.mib.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.snmp.mib.interfaces.Snmp4jInterface;
import com.snmp.mib.model.MibObject;
import com.snmp.mib.model.SearchCriteria;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * <Description>snmp服务 <br>
 * 
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.service <br>
 */
public class SnmpDataService implements Snmp4jInterface {
    /** 默认版本 */
    public static final int DEFAULT_VERSION = SnmpConstants.version2c;
    /** udp */
    public static final String DEFAULT_PROTOCOL = "udp";
    /** port */
    public static final int DEFAULT_PORT = 161;
    /** timeout */
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    /** retry times */
    public static final int DEFAULT_RETRY = 3;
    /** snmp */
    private Snmp snmp = null;

    /**
     * 
     * Description: 建对象communityTarget<br>
     * 
     * @author yx<br>
     * @param search
     *            SearchCriteria
     * @param community 
     * @return <br>
     */
    public CommunityTarget createDefault(SearchCriteria search, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + search.getHost() + "/" + search.getPort());
        Target target = null;

        if (search.getVersion() == 3) {
            snmp.getUSM().addUser(new OctetString(search.getUsername()),
                    new UsmUser(new OctetString(search.getUsername()), AuthMD5.ID,
                            new OctetString(search.getAuthPass()), PrivDES.ID, new OctetString(search.getPriPass())));
            target = new UserTarget();
            // 设置安全级别
            ((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
            ((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
            target.setVersion(SnmpConstants.version3);
        }
        else {
            target = new CommunityTarget();
            if (search.getVersion() == 1) {
                target.setVersion(SnmpConstants.version1);
                ((CommunityTarget) target).setCommunity(new OctetString(community));
            }
            else {
                target.setVersion(SnmpConstants.version2c);
                ((CommunityTarget) target).setCommunity(new OctetString(community));
            }

        }
        target.setAddress(address);
        // target.setVersion(search.getVersion());
        target.setTimeout(search.getTimeout()); // milliseconds
        target.setRetries(search.getRetransmits());
        return (CommunityTarget) target;
    }

    /**
     * 
     * Description:创建PDU，snmp v1,v2,v3 ，pdu不同<br>
     * 
     * @author yx<br>
     * @param target 
     * @return <br>
     */
    public PDU createPDU(Target target) {
        PDU request;
        if (target.getVersion() == 3) {
            request = new ScopedPDU();
            ScopedPDU scopedPDU = (ScopedPDU) request;
            OctetString contextEngineId = new OctetString(MPv3.createLocalEngineID());
            scopedPDU.setContextEngineID(contextEngineId);
            // scopedPDU.setContextName(this.contextName);//must be same as SNMP agent
        }
        else {
            request = new PDU();
        }
        return request;
    }

    /**
     * 
     * Description:根据oid获取信息 <br>
     * 
     * @author yx<br>
     * @param search
     * @return <br>
     */
    @Override
    public String get(SearchCriteria search) {
        // 设置 目标
        CommunityTarget target = createDefault(search, search.getReadCommunity());
        Snmp snmp = null;
        String result = "";
        try {
            // PDU 对象
            PDU pdu = createPDU(target);
            pdu.add(new VariableBinding(new OID(search.getOid())));

            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            // 操作类型
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            PDU response = respEvent.getResponse();
            // 解析Response
            if (response == null) {
                result = "response is null";
            }
            else {
                if (response.getErrorStatus() == PDU.noError) {
                    Vector<? extends VariableBinding> vbs = response.getVariableBindings();
                    for (VariableBinding vb : vbs) {
                        if ("noSuchInstance".equals(vb.getVariable().toString())) {
                            result += "Error:" + vb + " ," + vb.getVariable().getSyntaxString();
                        }
                        else {
                            result += vb + " ," + vb.getVariable().getSyntaxString();
                        }
                    }
                }
                else {
                    result += "Error:" + response.getErrorStatusText();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Exception:" + e);
        }
        finally {
            if (snmp != null) {
                try {
                    snmp.close();
                }
                catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
        return result;
    }

    /**
     * 
     * Description: 根据oid获取下一节点信息 <br>
     * 
     * @author yx<br>
     * @param search
     * @return <br>
     */
    @Override
    public String getNext(SearchCriteria search) {

        CommunityTarget target = createDefault(search, search.getReadCommunity());
        Snmp snmp = null;
        String result = "";
        try {
            // PDU pdu = new PDU();
            PDU pdu = createPDU(target);
            pdu.add(new VariableBinding(new OID(search.getOid())));
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GETNEXT);
            ResponseEvent respEvent = snmp.send(pdu, target);
            System.out.println("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();

            if (response == null) {
                result = "response is null";
            } else {
                if (response.getErrorStatus() == PDU.noError) {
                    Vector<? extends VariableBinding> vbs = response.getVariableBindings();
                    for (VariableBinding vb : vbs) {
                        result += vb + " ," + vb.getVariable().getSyntaxString();
                    }
                }
                else {
                    result += "Error:" + response.getErrorStatusText();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Exception:" + e);
        }
        finally {
            if (snmp != null) {
                try {
                    snmp.close();
                }
                catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
        return result;
    }

    /**
     * 
     * Description: 获取列表信息，一次获取多条信息<br>
     * 
     * @author yx<br>
     * @param search
     *            SearchCriteria
     * @param oidList
     *            OIDlist
     * @return <br>
     */
    @Override
    public String getList(SearchCriteria search, List<String> oidList) {
        CommunityTarget target = createDefault(search, search.getReadCommunity());
        String result = "";
        try {
            // PDU pdu = new PDU();
            PDU pdu = createPDU(target);
            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            System.out.println("PeerAddress:" + respEvent.getPeerAddress());
            PDU response = respEvent.getResponse();
            if (response == null) {
                System.out.println("response is null");
            }
            else {
                for (int i = 0; i < response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    result += vb + " ," + vb.getVariable().getSyntaxString() + "\n";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Exception:" + e);
        }
        finally {
            if (snmp != null) {
                try {
                    snmp.close();
                }
                catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
        return result;
    }

    /**
     * 
     * Description:walk命令 <br>
     * 
     * @author yx<br>
     * @param search
     * @return <br>
     */
    @Override
    public String walk(SearchCriteria search) {
        CommunityTarget target = createDefault(search, search.getReadCommunity());
        TransportMapping transport = null;
        String result = "";
        try {
            transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();
            // PDU pdu = new PDU();
            PDU pdu = createPDU(target);
            OID targetOID = new OID(search.getOid());
            pdu.add(new VariableBinding(targetOID));
            boolean finished = false;
            while (!finished) {
                VariableBinding vb = null;
                ResponseEvent respEvent = snmp.getNext(pdu, target);
                PDU response = respEvent.getResponse();
                if (null == response) {
                    System.out.println("responsePDU == null");
                    finished = true;
                    break;
                }
                else {
                    vb = response.get(0);
                }
                // walk命令是否完成
                finished = checkWalkFinished(targetOID, pdu, vb);
                if (!finished) {
                    System.out.println(vb.getOid() + " = " + vb.getVariable());
                    result += vb.getOid() + " = " + vb.getVariable() + "\n";
                    pdu.setRequestID(new Integer32(0));
                    pdu.set(0, vb);
                }
                else {
                    System.out.println("SNMP walk has finished.");
                    snmp.close();
                }
            }
            System.out.println("----> demo end <----");
        }
        catch (Exception e) {
            result = "SNMP walk Exception: " + e;
            e.printStackTrace();
            System.out.println("SNMP walk Exception: " + e);

        }
        finally {
            if (snmp != null) {
                try {
                    snmp.close();
                }
                catch (IOException ex1) {
                    snmp = null;
                }
            }
        }
        return result;
    }

    /**
     * 
     * Description: walk操作时，判断是否已运行完<br>
     * 
     * @author yx<br>
     * @param targetOID 
     * @param pdu 
     * @param vb 
     * @return <br>
     */
    private static boolean checkWalkFinished(OID targetOID, PDU pdu, VariableBinding vb) {
        boolean isFinished = false;
        if (pdu.getErrorStatus() != 0) {
            System.out.println("[true] responsePDU.getErrorStatus() != 0 ");
            System.out.println(pdu.getErrorStatusText());
            isFinished = true;
        }
        else if (vb.getOid() == null) {
            System.out.println("[true] vb.getOid() == null");
            isFinished = true;
        }
        else if (vb.getOid().size() < targetOID.size()) {
            System.out.println("[true] vb.getOid().size() < targetOID.size()");
            isFinished = true;
        }
        else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) {
            System.out.println("[true] targetOID.leftMostCompare() != 0");
            isFinished = true;
        }
        else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            System.out.println("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            isFinished = true;
        }
        else if (vb.getOid().compareTo(targetOID) <= 0) {
            System.out.println("[true] Variable received is not " + "lexicographic successor of requested " + "one:");
            System.out.println(vb.toString() + " <= " + targetOID);
            isFinished = true;
        }
        return isFinished;

    }

    /**
     * snmp4j set功能
     * 
     * @return result 返回结果
     */
    @Override
    public String set(SearchCriteria search) throws IOException {
        CommunityTarget target = createDefault(search, search.getSetCommunity());
        String result = "";
        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        snmp.listen();
        PDU pdu = createPDU(target);
        pdu.add(new VariableBinding(new OID(search.getOid()), new OctetString(search.getSetValue())));
        pdu.setType(PDU.SET);
        ResponseEvent resEvt = snmp.send(pdu, target);
        PDU response = resEvt.getResponse();
        if (response == null) {
            result = "response is null";
        }
        else {
            if (response.getErrorIndex() == PDU.noError && response.getErrorStatus() == PDU.noError) {// 判断返回报文是否正确
                VariableBinding vb = (VariableBinding) response.getVariableBindings().firstElement();
                Variable var = vb.getVariable();
                if (search.getSetValue().equals(var.toString())) {// 比较返回值和设置值
                    System.out.println("SET操作成功 ！");
                    result = "SET操作成功 !";
                }
                else {
                    System.out.println("SET操作失败 ！");
                    result = "SET操作失败 !";
                }
            }
            else {
                try {
                    result = "错误信息:" + response.getErrorStatusText();
                    throw new Exception("错误信息:" + response.getErrorStatusText());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        snmp.close();
        return result;

    }

    /**
     * 
     * Description: 实现tableView功能 <br>
     * 
     * @author XXX<br>
     * @param search
     * @return <br>
     */
    @Override
    public String tableView(SearchCriteria search) {
        CommunityTarget target = createDefault(search, search.getReadCommunity());
        String result = "";
        List<MibObject> moList = new ArrayList();
        int columnCount = 0;
        try {
            // 实例化接口
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            // 监听
            snmp.listen();
            TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));// GETBULK
            utils.setMaxNumRowsPerPDU(5);
            OID[] columnOIDs = new OID[] { new OID(search.getOid()) };
            // utils.getTable(target, columnOIDs, listener, null, null, null);
            // 取表数据
            List<TableEvent> rowvalues = utils.getTable(target, columnOIDs, new OID("0"), new OID("50"));
            int columnTemp = 0;
            for (int i = 0; i < rowvalues.size(); i++) {
                // 取list中的一行
                TableEvent te = (TableEvent) rowvalues.get(i);
                // 对每一行结果进行再次拆分
                VariableBinding[] vb = te.getColumns();
                if (vb != null) {
                    for (int j = 0; j < vb.length; j++) {
                        result += vb[j].getOid() + "=" + vb[j].getVariable().toString() + ","
                                + vb[j].getVariable().getSyntaxString();

                        MibObject mo = new MibObject();
                        String instanceId = vb[j].getOid().toString().replaceAll(search.getOid() + ".", "");
                        if (columnTemp != Integer.valueOf(instanceId.substring(0, instanceId.indexOf(".")))) {
                            columnTemp = Integer.valueOf(instanceId.substring(0, instanceId.indexOf(".")));
                            columnCount++;
                        }
                        mo.setDesc(instanceId.substring(0, instanceId.indexOf(".")));
                        mo.setId(instanceId.substring(instanceId.indexOf('.') + 1, instanceId.length()));
                        mo.setOid(vb[j].getOid().toString());
                        mo.setValue(vb[j].getVariable().toString());
                        mo.setSyntax(vb[j].getVariable().getSyntaxString());
                        // mo.setName(name);
                        moList.add(mo);
                    }
                }
                else {
                    throw new NullPointerException("被监控系统的网络不通或IP或其它相关配置错误！");
                }
                result += "\n";
            }
            System.out.println(columnCount);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray json = JSONArray.fromObject(moList);
        return json.toString();
    }

    /**
     * 
     * Description: <br>
     * 
     * @author snmp4j 批量set功能<br>
     * @param jsonParam
     *            需要进行set操作的数据
     * @return <br>
     */
    @Override
    public String batchSet(String jsonParam) throws IOException {
        String result = "";
        List<MibObject> moList = new ArrayList();
        ParseJson parseJson = new ParseJson();
        moList = parseJson.json2List(jsonParam.toString());
        if(moList.size()==0) {
            result="No data to be saved";
            return result;
        }
        JSONObject obj = new JSONObject().fromObject(jsonParam);
        // 开始构造SearchCriteria
        SearchCriteria search = new SearchCriteria();
        search.setHost(obj.get("host").toString());
        search.setVersion(Integer.valueOf(obj.get("version").toString()));
        search.setPort(Integer.valueOf(obj.get("port").toString()));
        search.setTimeout(Integer.valueOf(obj.get("timeout").toString()));
        search.setRetransmits(Integer.valueOf(obj.get("retransmits").toString()));
        search.setNonRepeaters(Integer.valueOf(obj.get("nonRepeaters").toString()));
        search.setMaxRepetitions(Integer.valueOf(obj.get("maxRepetitions").toString()));

        CommunityTarget target = createDefault(search, obj.get("setCommunity").toString());
        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        snmp.listen();
        PDU pdu = createPDU(target);
        // pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"), new
        // OctetString("1")));
        for (int i = 0; i < moList.size(); i++) {
            if ("DisplayString".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new OctetString("")));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new OctetString(moList.get(i).getValue())));
                }
            }
            else if ("Integer".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Integer32()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Integer32(Integer.valueOf(moList.get(i).getValue()))));
                }

            }
            else if ("PhysAddress".equals(moList.get(i).getSyntax())
                    || "NetworkAddress".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new IpAddress()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new OctetString(moList.get(i).getValue())));
                }

            }
            else if ("IpAddress".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new IpAddress()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new IpAddress(moList.get(i).getValue())));
                }

            }
            else if ("TimeTicks".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new TimeTicks()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new TimeTicks(Long.valueOf(moList.get(i).getValue()))));
                }
            }
            else if ("Gauge".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Gauge32()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Gauge32(Long.valueOf(moList.get(i).getValue()))));
                }

            } 
            else if ("Counter".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Counter32()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Counter32(Long.valueOf(moList.get(i).getValue()))));
                }

            }

        }
        pdu.setType(PDU.SET);
        ResponseEvent resEvt = snmp.send(pdu, target);
        PDU response = resEvt.getResponse();
        if (response == null) {
            result = "response is null";
        }
        else {
            if (response.getErrorIndex() == PDU.noError && response.getErrorStatus() == PDU.noError) {// 判断返回报文是否正确
                VariableBinding vb = (VariableBinding) response.getVariableBindings().firstElement();
                // Variable var = vb.getVariable();
                result = "SET操作成功 !";

            }
            else {
                try {
                    result = "错误信息:" + response.getErrorStatusText();
                    throw new Exception("错误信息:" + response.getErrorStatusText());
                } 
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        snmp.close();
        return result;
    }

    /**
     * snmp4j getBulk功能
     * 
     * @param search
     * @return
     */
    /**
     * 
     * Description: snmp4j getBulk功能 <br>
     * 
     * @author XXX<br>
     * @param search
     * @return <br>
     */
    @Override
    public String getBulk(SearchCriteria search) {
        // 设置 目标
        CommunityTarget target = createDefault(search, search.getReadCommunity());
        String result = "";
        try {
            // PDU 对象
            PDU pdu = createPDU(target);
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            // 操作类型
            pdu.setType(PDU.GETBULK);
            // snmp getBulk独有
            pdu.setMaxRepetitions(search.getMaxRepetitions()); // 每个OID通过GETBULK方式获取多少个数据
            pdu.setNonRepeaters(search.getNonRepeaters());

            pdu.add(new VariableBinding(new OID(search.getOid())));
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response == null) {
                System.out.println("TimeOut...");
            } 
            else {
                if (response.getErrorStatus() == PDU.noError) {
                    Vector<? extends VariableBinding> vbs = response.getVariableBindings();
                    for (VariableBinding vb : vbs) {
                        if ("noSuchInstance".equals(vb.getVariable().toString())) {
                            result += "Error:" + vb + " ," + vb.getVariable().getSyntaxString();
                        }
                        else {
                            result += vb + " ," + vb.getVariable().getSyntaxString();
                        }
                    }
                } 
                else {
                    result += "Error:" + response.getErrorStatusText();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Exception:" + e);
        }
        finally {
            if (snmp != null) {
                try {
                    snmp.close();
                }
                catch (IOException ex1) {
                    snmp = null;
                }
            }

        }
        return result;
    }

    /**
     * 
     * Description: <br>
     * 
     * @author snmp4j tableview中批量set功能<br>
     * @param jsonParam
     *            需要进行set操作的数据
     * @return <br>
     */
    @Override
    public String tableBatchSet(String jsonParam) throws IOException {
        String result = "";
        List<MibObject> moList = new ArrayList();
        ParseJson parseJson = new ParseJson();
        moList = parseJson.json2ListForTable(jsonParam.toString());
        if(moList.size()==0) {
            result="No data to be saved";
            return result;
        }
        JSONObject obj = new JSONObject().fromObject(jsonParam);
        // 开始构造SearchCriteria
        SearchCriteria search = new SearchCriteria();
        search.setHost(obj.get("host").toString());
        search.setVersion(Integer.valueOf(obj.get("version").toString()));
        search.setPort(Integer.valueOf(obj.get("port").toString()));
        search.setTimeout(Integer.valueOf(obj.get("timeout").toString()));
        search.setRetransmits(Integer.valueOf(obj.get("retransmits").toString()));
        search.setNonRepeaters(Integer.valueOf(obj.get("nonRepeaters").toString()));
        search.setMaxRepetitions(Integer.valueOf(obj.get("maxRepetitions").toString()));

        CommunityTarget target = createDefault(search, obj.get("setCommunity").toString());
        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        snmp.listen();
        PDU pdu = createPDU(target);
        // pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"), new
        // OctetString("1")));
        for (int i = 0; i < moList.size(); i++) {
            if ("DisplayString".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new OctetString("")));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new OctetString(moList.get(i).getValue())));
                }
            } 
            else if ("INTEGER".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Integer32()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Integer32(Integer.valueOf(moList.get(i).getValue()))));
                }

            } 
            else if ("PhysAddress".equals(moList.get(i).getSyntax())
                    || "NetworkAddress".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new IpAddress()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new OctetString(moList.get(i).getValue())));
                }

            } 
            else if ("IpAddress".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new IpAddress()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new IpAddress(moList.get(i).getValue())));
                }

            } 
            else if ("TimeTicks".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new TimeTicks()));
                }
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new TimeTicks(Long.valueOf(moList.get(i).getValue()))));
                }
            } 
            else if ("Gauge".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Gauge32()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Gauge32(Long.valueOf(moList.get(i).getValue()))));
                }

            }
            else if ("Counter".equals(moList.get(i).getSyntax())) {
                if ("".equals(moList.get(i).getValue()) || moList.get(i).getValue() == null) {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()), new Counter32()));
                } 
                else {
                    pdu.add(new VariableBinding(new OID(moList.get(i).getOid()),
                            new Counter32(Long.valueOf(moList.get(i).getValue()))));
                }

            }

        }
        pdu.setType(PDU.SET);
        ResponseEvent resEvt = snmp.send(pdu, target);
        PDU response = resEvt.getResponse();
        if (response == null) {
            result = "response is null";
        }
        else {
            if (response.getErrorIndex() == PDU.noError && response.getErrorStatus() == PDU.noError) {// 判断返回报文是否正确
                VariableBinding vb = (VariableBinding) response.getVariableBindings().firstElement();
                Variable var = vb.getVariable();
                result = "SET操作成功 !";

            }
            else {
                try {
                    result = "错误信息:" + response.getErrorStatusText();
                    throw new Exception("错误信息:" + response.getErrorStatusText());
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        snmp.close();
        return result;
    }
}
