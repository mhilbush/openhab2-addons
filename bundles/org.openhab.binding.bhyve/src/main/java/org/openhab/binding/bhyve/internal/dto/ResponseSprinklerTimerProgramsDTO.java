/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.bhyve.internal.dto;

import org.openhab.binding.bhyve.internal.dto.common.FrequencyDTO;
import org.openhab.binding.bhyve.internal.dto.common.RunTimeDTO;
import org.openhab.binding.bhyve.internal.dto.common.WateringPlanDTO;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link ResponseSprinklerTimerProgramsDTO} is responsible for
 *
 * @author Mark Hilbush - Initial contribution
 */
public class ResponseSprinklerTimerProgramsDTO {

    @SerializedName("budget")
    public Integer budget;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("device_id")
    public String deviceId;

    @SerializedName("enabled")
    public Boolean enabled;

    @SerializedName("frequency")
    public FrequencyDTO frequency;

    @SerializedName("id")
    public String id;

    @SerializedName("is_smart_program")
    public Boolean isSmartProgram;

    @SerializedName("lock_at")
    public String lockAt;

    @SerializedName("long_term_program")
    public Object longTermProgram;

    @SerializedName("name")
    public String name;

    @SerializedName("pending_timer_ack")
    public Boolean pendingTimerAck;

    @SerializedName("process_at")
    public String processAt;

    @SerializedName("program")
    public String program;

    @SerializedName("run_times")
    public RunTimeDTO[] runTimes;

    @SerializedName("start_times")
    public String[] startTimes;

    @SerializedName("watering_plan")
    public WateringPlanDTO[] wateringPlan;
}

//@formatter:off
/*
[
 {
   "budget": 100,
   "created_at": "2019-03-18T13:28:21.500Z",
   "device_id": "5ad72e5a4f0c72d7d6257c5b",
   "enabled": true,
   "frequency": {
     "days": [
       0,
       2,
       3,
       4,
       5,
       6
     ],
     "type": "days"
   },
   "id": "5c8f9cf54f0c5c18c0759e01",
   "is_smart_program": true,
   "lock_at": "2019-03-19T10:00:00.000Z",
   "long_term_program": {
     "budgets": [
       0,
       0,
       0,
       90,
       100,
       60,
       60,
       100,
       80,
       0,
       0,
       0
     ],
     "frequency": {
       "intervals": [
         5,
         3,
         3,
         2,
         2,
         1,
         1,
         2,
         2,
         3,
         3,
         5
       ],
       "type": "interval"
     },
     "run_times": [
       {
         "run_time": 35,
         "station": 1
       }
     ],
     "start_times": [
       "08:00"
     ]
   },
   "name": "Smart Watering",
   "pending_timer_ack": true,
   "process_at": "2019-03-19T13:02:00.000Z",
   "program": "e",
   "run_times": [
     {
       "run_time": 32,
       "station": 1
     }
   ],
   "start_times": [
     "08:00"
   ],
   "watering_plan": [
     {
       "date": "2019-03-18T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-18T04:00:00.000Z",
           "delta": -0.00029999999999996696,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.04800000000000001,
           "etc": 0.08227474273714744,
           "eto": 0.06601444368971876,
           "final_water_level": 0.645,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.6453,
           "mbf_raw": 0.0,
           "mbo_raw": 0.00029999999999996696,
           "net_irrigation": 0.0,
           "rainfall": 0.060000000000000005,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "system_restricted"
         }
       ]
     },
     {
       "date": "2019-03-19T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-19T04:00:00.000Z",
           "delta": -0.00029999999999996696,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.0,
           "etc": 0.11497069883690482,
           "eto": 0.09224856221774207,
           "final_water_level": 0.645,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.645,
           "mbf_raw": 0.0,
           "mbo_raw": 0.0,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-20T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-20T04:00:00.000Z",
           "delta": -0.00029999999999996696,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.0,
           "etc": 0.1274045201096245,
           "eto": 0.10222503576173504,
           "final_water_level": 0.645,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.645,
           "mbf_raw": 0.0,
           "mbo_raw": 0.0,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-21T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-21T04:00:00.000Z",
           "delta": 0.10351675209008115,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.12,
           "etc": 0.12067645585140263,
           "eto": 0.09682666678069585,
           "final_water_level": 0.7488167520900811,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.645,
           "mbf_raw": 0.10381675209008112,
           "mbo_raw": 0.0,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0.15,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-22T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-22T04:00:00.000Z",
           "delta": 0.061377524517653326,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.0,
           "etc": 0.14663243551391156,
           "eto": 0.1176528584020589,
           "final_water_level": 0.7066775245176533,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.7488167520900811,
           "mbf_raw": 0.06167752451765329,
           "mbo_raw": 0.10381675209008112,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-23T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-23T04:00:00.000Z",
           "delta": 0.016350308990402906,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.0,
           "etc": 0.1495204234687342,
           "eto": 0.11997008130519594,
           "final_water_level": 0.6616503089904029,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.7066775245176533,
           "mbf_raw": 0.016650308990402873,
           "mbo_raw": 0.06167752451765329,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-24T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 32,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-24T04:00:00.000Z",
           "delta": -0.00029999999999996696,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.10449320794148378,
           "effective_rainfall": 0.0,
           "etc": 0.12910696624039797,
           "eto": 0.10359102039439172,
           "final_water_level": 0.645,
           "gross_irrigation": 0.26666666666666666,
           "initial_water_level": 0.6616503089904029,
           "mbf_raw": 0.0,
           "mbo_raw": 0.016650308990402873,
           "net_irrigation": 0.10449320794148378,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-25T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.21319349398285792,
           "date": "2019-03-25T04:00:00.000Z",
           "delta": 0.3178934939828578,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.42479024097428675,
           "etc": 0.1065967469914289,
           "eto": 0.08552958924775435,
           "final_water_level": 0.9631934939828578,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.645,
           "mbf_raw": 0.3181934939828578,
           "mbo_raw": 0.0,
           "net_irrigation": 0.0,
           "rainfall": 1.1099999999999999,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.21319349398285792,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-26T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.27849434339265056,
           "date": "2019-03-26T04:00:00.000Z",
           "delta": 0.38319434339265057,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.20454802110611803,
           "etc": 0.13924717169632522,
           "eto": 0.11172717493954902,
           "final_water_level": 1.0284943433926506,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.9631934939828578,
           "mbf_raw": 0.38349434339265054,
           "mbo_raw": 0.3181934939828578,
           "net_irrigation": 0.0,
           "rainfall": 1.08,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.27849434339265056,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-27T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.11011935737196343,
           "date": "2019-03-27T04:00:00.000Z",
           "delta": 0.21481935737196345,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.0,
           "etc": 0.1683749860206871,
           "eto": 0.13509833836771482,
           "final_water_level": 0.8601193573719634,
           "gross_irrigation": 0.0,
           "initial_water_level": 1.0284943433926506,
           "mbf_raw": 0.21511935737196342,
           "mbo_raw": 0.38349434339265054,
           "net_irrigation": 0.0,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.11011935737196343,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-28T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.0018228854638842051,
           "date": "2019-03-28T04:00:00.000Z",
           "delta": 0.10652288546388422,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.0,
           "etc": 0.10829647190807919,
           "eto": 0.08689339047113466,
           "final_water_level": 0.7518228854638842,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.8601193573719634,
           "mbf_raw": 0.10682288546388419,
           "mbo_raw": 0.21511935737196342,
           "net_irrigation": 0.0,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.0018228854638842051,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-29T04:00:00.000Z",
       "run_times": [
         {
           "run_time": 29,
           "station": 1
         }
       ],
       "start_times": [
         "08:00"
       ],
       "zone_forecasts": [
         {
           "daily_surplus": 0,
           "date": "2019-03-29T04:00:00.000Z",
           "delta": 0.10310257237998455,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [
             0
           ],
           "effective_irrigation": 0.09469696969696968,
           "effective_rainfall": 0.0,
           "etc": 0.0981172827808694,
           "eto": 0.07872595675952745,
           "final_water_level": 0.7484025723799845,
           "gross_irrigation": 0.24166666666666667,
           "initial_water_level": 0.7518228854638842,
           "mbf_raw": 0.10340257237998451,
           "mbo_raw": 0.10682288546388419,
           "net_irrigation": 0.09469696969696968,
           "rainfall": 0,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-30T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.12914986140869222,
           "date": "2019-03-30T04:00:00.000Z",
           "delta": 0.23384986140869224,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.19532221973305386,
           "etc": 0.06457493070434613,
           "eto": 0.051812719006228365,
           "final_water_level": 0.8791498614086922,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.7484025723799845,
           "mbf_raw": 0.2341498614086922,
           "mbo_raw": 0.10340257237998451,
           "net_irrigation": 0.0,
           "rainfall": 0.6000000000000001,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.12914986140869222,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-03-31T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.0354564994898835,
           "date": "2019-03-31T04:00:00.000Z",
           "delta": 0.1401564994898835,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.048,
           "etc": 0.1416933619188088,
           "eto": 0.11368991443072425,
           "final_water_level": 0.7854564994898835,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.8791498614086922,
           "mbf_raw": 0.14045649948988348,
           "mbo_raw": 0.2341498614086922,
           "net_irrigation": 0.0,
           "rainfall": 0.06,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.0354564994898835,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     },
     {
       "date": "2019-04-01T04:00:00.000Z",
       "run_times": [],
       "start_times": [],
       "zone_forecasts": [
         {
           "daily_surplus": 0.167851309366281,
           "date": "2019-04-01T04:00:00.000Z",
           "delta": 0.272551309366281,
           "device_id": "5ad72e5a4f0c72d7d6257c5b",
           "direct_runoff": [],
           "effective_irrigation": 0.0,
           "effective_rainfall": 0.21632046455953813,
           "etc": 0.08392565468314057,
           "eto": 0.06733907905252544,
           "final_water_level": 0.917851309366281,
           "gross_irrigation": 0.0,
           "initial_water_level": 0.7854564994898835,
           "mbf_raw": 0.272851309366281,
           "mbo_raw": 0.14045649948988348,
           "net_irrigation": 0.0,
           "rainfall": 1.11,
           "soak_runoff": [],
           "station": 1,
           "total_direct_runoff": 0,
           "total_scheduling_losses": 0.167851309366281,
           "total_soak_runoff": 0,
           "water_rule": "as-needed"
         }
       ]
     }
   ]
 }
]
*/
//@formatter:on
