//package com.leon.datalink.driver.snmp;
//
//import akka.actor.ActorSystem;
//import com.leon.datalink.core.utils.Loggers;
//import com.leon.datalink.core.utils.StringUtils;
//import org.snmp4j.CommunityTarget;
//import org.snmp4j.PDU;
//import org.snmp4j.Snmp;
//import org.snmp4j.event.ResponseEvent;
//import org.snmp4j.event.ResponseListener;
//import org.snmp4j.smi.*;
//import org.snmp4j.transport.DefaultUdpTransportMapping;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
///**
// * @ClassNameSnmpProtocol
// * @Description
// * @Author Leon
// * @Date2022/3/31 8:56
// * @Version V1.0
// **/
//public class SnmpProtocol extends AbstractBaseProtocol {
//
//    /**
//     * default community
//     */
//    private String defaultCommunity = "public";
//
//    /**
//     * SNMP client
//     */
//    private Snmp snmp;
//
//    /**
//     * community
//     */
//    private CommunityTarget communityTarget;
//
//    /**
//     * init
//     *
//     * @throws Exception
//     */
//    public SnmpProtocol(Device device, ActorSystem actorSystem) throws Exception {
//        super(device, actorSystem);
//    }
//
//    private void create(String ip, int port){
//        this.communityTarget = createDefault(ip, port, defaultCommunity);
//        DefaultUdpTransportMapping transport = null;
//        try {
//            transport = new DefaultUdpTransportMapping();
//            this.snmp = new Snmp(transport);
//            this.snmp.listen();
//            this.connectStatusEnum = ConnectStatusEnum.CONNECT;
//        } catch (IOException e) {
//            Loggers.SNMP.error("create client error {}", e.getMessage());
//            this.connectStatusEnum = ConnectStatusEnum.CONNECT_ERROR;
//        }
//    }
//
//    @Override
//    public void create() {
//        if (device == null) return;
//        this.defaultCommunity = StringUtils.isEmpty(device.getUsername()) ? this.defaultCommunity : device.getUsername();
//        this.isClose = false;
//        this.create(device.getIp(), device.getPort());
//    }
//
//    @Override
//    public void update(Device device) throws IOException {
//        this.snmp.close();
//        this.device = device;
//        this.create();
//    }
//
//    @Override
//    public void close() throws IOException {
//        this.isClose = false;
//        this.snmp.close();
//        this.snmp = null;
//        this.connectStatusEnum = ConnectStatusEnum.DISCONNECT;
//    }
//
//    @Override
//    public Tag read(Tag tag) {
//        List<Tag> snmpResults = this.snmpAsyncGetList(Arrays.asList(tag));
//        if (snmpResults.size() > 0) {
//            return snmpResults.get(0);
//        }
//        return null;
//    }
//
//    @Override
//    public List<Tag> readList(List<Tag> tags) {
//        return this.snmpAsyncGetList(tags);
//    }
//
//    @Override
//    public boolean write(Tag param) {
//        return false;
//    }
//
//    /**
//     * create communityTarget
//     *
//     * @param ip
//     * @param community
//     * @return CommunityTarget
//     */
//    public CommunityTarget createDefault(String ip, int port, String community) {
//        Address address = GenericAddress.parse(Constants.DEFAULT_PROTOCOL + ":" + ip
//                + "/" + port);
//        CommunityTarget target = new CommunityTarget();
//        target.setCommunity(new OctetString(community));
//        target.setAddress(address);
//        target.setVersion(Constants.DEFAULT_VERSION);
//        target.setTimeout(Constants.DEFAULT_TIMEOUT); // milliseconds
//        target.setRetries(Constants.DEFAULT_RETRY);
//        return target;
//    }
//
//    /**
//     * async get value
//     *
//     * @param tags
//     */
//    public List<Tag> snmpAsyncGetList(List<Tag> tags) {
//        try {
//            PDU pdu = new PDU();
//            for (Tag tag : tags) {
//                pdu.add(new VariableBinding(new OID(tag.getAddr())));
//            }
//
//            final CountDownLatch latch = new CountDownLatch(1);
//            ResponseListener listener = new ResponseListener() {
//                public void onResponse(ResponseEvent event) {
//                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
//                    PDU response = event.getResponse();
//                    if (response == null) {
//
//                    } else if (response.getErrorStatus() != 0) {
//                        Loggers.CORE.error("response status"
//                                + response.getErrorStatus() + " Text:"
//                                + response.getErrorStatusText());
//                    } else {
//                        for (int i = 0; i < response.size(); i++) {
//                            VariableBinding vb = response.get(i);
//                            Optional<Tag> tag = tags.stream().filter(x -> x.getAddr().equals("." + vb.getOid().toString())).findFirst();
//                            if (tag.isPresent()) {
//                                tag.get().setTimeStamp(new Date());
//                                tag.get().setValue(vb.getVariable().toString());
//                            }
//                        }
//                        latch.countDown();
//                    }
//                }
//            };
//
//            pdu.setType(PDU.GET);
//            this.snmp.send(pdu, communityTarget, null, listener);
//            latch.await(30, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Loggers.SNMP.error("SNMP Asyn GetList Exception: {} .", e);
//        }
//        return tags;
//
//    }
//
//    @Override
//    protected String protocolPath() {
//        return "/snmp";
//    }
//}
