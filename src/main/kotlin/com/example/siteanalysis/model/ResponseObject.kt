package com.example.siteanalysis.model

//{
//    "Time": "Jun 1, 2023 5:06:52 PM",
//    "Destination": "SAG_192.129.41.35 (192.129.41.35)",
//    "Rule": 169,
//    "Interface Direction": "inbound",
//    "rule_uid": "7fbdc415-750a-4f01-93aa-81c2cffc1f1b",
//    "Type": "Connection",
//    "Interface": "bond2.1959",
//    "Policy Date": "2023-06-01T10:55:42Z",
//    "Service ID": "domain-udp",
//    "Action": "Accept",
//    "ID": "05e629d8-efbc-20e8-6478-b40c0000004a",
//    "Interface Name": "bond2.1959",
//    "Layer Name": "Network",
//    "Source Port": 20968,
//    "Product Family": "Access",
//    "Blade": "Firewall",
//    "Sequence Number": 1079,
//    "Source Zone": "Internal",
//    "Source": "10.220.211.90",
//    "Access Rule Name": "white_DNS",
//    "Policy Name": "sag-se-bp",
//    "id_generated_by_indexer": "FALSE",
//    "Destination Zone": "External",
//    "Database Tag": "{3775D95A-DBDF-3545-A954-76ECA915476E}",
//    "Log Server Origin": "DLS-P-SAG-Energy (155.45.240.229)",
//    "Service": "domain-udp",
//    "Origin": "FW-SAG-Energy-DE-01",
//    "Marker": "@A@@B@1685626284@C@15678463",
//    "Destination Port": 53,
//    "Domain": "CST-P-SAG-Energy",
//    "Protocol": "UDP (17) (17)",
//    "logid": 0,
//    "first": "TRUE",
//    "Policy Management": "DMS-P-SAG-Energy",
//    "Direction of Connection": null,
//    "log_delay": null
//}
class ResponseObject {

    var time: String? = null
    var Destination: String? = null
    var Rule: Int? = null
    var InterfaceDirection: String? = null
    var rule_uid: String? = null
    var Type: String? = null
    var Interface: String? = null
    var PolicyDate: String? = null
    var ServiceID: String? = null
    var Action: String? = null
    var ID: String? = null
    var InterfaceName: String? = null
    var LayerName: String? = null
    var SourcePort: Int? = null
    var ProductFamily: String? = null
    var Blade: String? = null
    var SequenceNumber: Int? = null
    var SourceZone: String? = null
    var Source: String? = null
    var AccessRuleName: String? = null
    var PolicyName: String? = null
    var id_generated_by_indexer: String? = null
    var DestinationZone: String? = null
    var DatabaseTag: String? = null
    var LogServerOrigin: String? = null
    var Service: String? = null
    var Origin: String? = null
    var Marker: String? = null
    var DestinationPort: Int? = null
    var Domain: String? = null
    var Protocol: String? = null
    var logid: Int? = null
    var first: String? = null
    var PolicyManagement: String? = null
    var DirectionofConnection: String? = null
    var log_delay: String? = null
}