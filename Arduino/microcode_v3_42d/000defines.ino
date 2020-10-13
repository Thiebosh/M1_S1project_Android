


/*
   factory default switch
*/
#define FACTORY_DEFAULT_SWITCH 31
#define FACTORY 1
#define NOT_FACTORY 0

/*
   board on slot lines. Inputs pull-up. Logical 0 indocates board present
*/
#define ONSLOT_0 39
#define ONSLOT_1 37
#define ONSLOT_2 35
#define ONSLOT_3 33
#define ONSLOT_4 38
#define ONSLOT_5 36
#define ONSLOT_6 34
#define ONSLOT_7 32

/*
   HMI interface
*/
#define CODEWHELL_DEBOUNCE 1

#define KBDBUTTON_X 0
#define KBDBUTTON_Y 0
#define KBDBUTTON_W 50
#define KBDBUTTON_H 50

#define KBZONE_X 0
#define KBZONE_Y 0
#define KBZONE_W 200
#define KBZONE_H 31

/*
   Calibration data for the raw touch data to the screen coordinates
*/
#define TS_MINX 208
#define TS_MINY 175
#define TS_MAXX 3808
#define TS_MAXY 3718


/*
   default values for variables
*/
#define DEF_CHANNEL_MODE 0x00 // 1 bit per channel, bit 7..0 = channel 7..0, 0= voltage, 1 = current
#define DEF_CHANNEL_ON  0x00  // 1 bit per channel, bit 7..0 = channel 7..0, 0= OFF, 1 = ON
#define DEF_CHANNEL_VALUE 0  // zero value for both I and V
#define DEF_CURRENT_RANGE 0 // -8uA .. 8uA range
#define DEF_MAX_VOLTAGE 3300  // value in mV
#define DEF_MIN_VOLTAGE -3300  // value in mV
#define DEF_MAX_CURRENT 8000  // value in uA, DAC value depends on range
#define DEF_MIN_CURRENT -8000  // value in uA, DAC value depends on range
#define DEF_MAX_RANGE 3 // can be 0..3
#define DEF_DISPLAY_UNIT 1 // Volt or mA
#define HIGH_COMPLIANCE_MODE 1 // limits current to HIGH_COMPLIANCE_MAX_CURRENT
#define HIGH_COMPLIANCE_MAX_CURRENT 5000 // value in uA, DAC value depends on range
#define HIGH_COMPLIANCE_MIN_CURRENT -5000 // value in uA, DAC value depends on range
/*
 * calibration
 */
#define DEF_ABSOLUTE_MAX_VALUE 8000  // value in mV, both V and I
#define DEF_ABSOLUTE_MIN_VALUE -8000  // value in mV, both V and I
#define LSB_VALUE 0.25  // value in mV

#define DEF_SELECTED_CHANNEL 0 // 1 bit per channel, bit 7..0 = channel 7..0, 0= unselected, 1 = selected
#define DEF_SELECTED_DIGIT 0 // 0=units, 1=tens, -1=1/10, -2=1/100, -3=1/1000
#define DEF_MIN_CHANNEL_LIMIT_SELECTED 0x00// 1 bit per channel, bit 7..0 = channel 7..0, 0= unselected, 1 = selected
#define DEF_MAX_CHANNEL_LIMIT_SELECTED 0x00// 1 bit per channel, bit 7..0 = channel 7..0, 0= unselected, 1 = selected


#define FACTORY_DEFAULT_USER_CONFIG_STORED 0x00  // 1 bit per config, bit 7..0 = config 7..0, 0= not saved, 1 = saved

/*
  2k x8 in 8 pages of 256 bytes
  FRAM base addresses for variables, effective 16 bit address is: uint16_t EFFECTIVE_ADDRESS = (FRAM_ADDRESS << 8) + XXX_BASE_ADDRESS + offset 
  to access data using FM24CL16B library: uint8_t pageAdd = highByte(EFFECTIVE_ADDRESS) and uint8_t address = lowByte(EFFECTIVE_ADDRESS)
*/

// 2 bytes per value
// coarse offset is added to data value , the others are stored in the corresponding register
// !!!! MUST also be defined in VIsource_v2.h

#define VOLTAGE_MODE_COARSE_OFFSET_BASE_ADDRESS 0x0000 // page 0, address 00 to 0F, 8x int = 16 bytes
#define VOLTAGE_MODE_FINE_OFFSET_BASE_ADDRESS 0x0010 // page 0, address 10 to 1F, 8x int = 16 bytes
#define VOLTAGE_MODE_COARSE_GAIN_BASE_ADDRESS 0x0020 // page 0, address 20 to 2F, 8x int = 16 bytes
#define VOLTAGE_MODE_FINE_GAIN_BASE_ADDRESS 0x0030 // page 0, address 30 to 3F, 8x int = 16 bytes

#define CURRENT_MODE_RANGE0_COARSE_OFFSET_BASE_ADDRESS 0x0040 // page 0, address 40 to 4F,  8x int = 16 bytes
#define CURRENT_MODE_RANGE0_FINE_OFFSET_BASE_ADDRESS 0x0050 // page 0, address 50 to 5F, 8x int = 16 bytes
#define CURRENT_MODE_RANGE0_COARSE_GAIN_BASE_ADDRESS 0x0060 // page 0, address 60 to 6F, 8x int = 16 bytes
#define CURRENT_MODE_RANGE0_FINE_GAIN_BASE_ADDRESS 0x0070 // page 0, address 70 to 7F, 8x int = 16 bytes

#define CURRENT_MODE_RANGE1_COARSE_OFFSET_BASE_ADDRESS 0x0080 // page 0, address 80 to 8F,  8x int = 16 bytes
#define CURRENT_MODE_RANGE1_FINE_OFFSET_BASE_ADDRESS 0x0090 // page 0, address 90 to 9F, 8x int = 16 bytes
#define CURRENT_MODE_RANGE1_COARSE_GAIN_BASE_ADDRESS 0x00A0 // page 0, address A0 to AF, 8x int = 16 bytes
#define CURRENT_MODE_RANGE1_FINE_GAIN_BASE_ADDRESS 0x00B0 // page 0, address B0 to BF, 8x int = 16 bytes

#define CURRENT_MODE_RANGE2_COARSE_OFFSET_BASE_ADDRESS 0x00C0 // page 0, address C0 to CF,  8x int = 16 bytes
#define CURRENT_MODE_RANGE2_FINE_OFFSET_BASE_ADDRESS 0x00D0 // page 0, address D0 to DF, 8x int = 16 bytes
#define CURRENT_MODE_RANGE2_COARSE_GAIN_BASE_ADDRESS 0x00E0 // page 0, address E0 to EF, 8x int = 16 bytes
#define CURRENT_MODE_RANGE2_FINE_GAIN_BASE_ADDRESS 0x00F0 // page 0, address F0 to FF, 8x int = 16 bytes

#define CURRENT_MODE_RANGE3_COARSE_OFFSET_BASE_ADDRESS 0x0100 // page 1, address 00 to0F,  8x int = 16 bytes
#define CURRENT_MODE_RANGE3_FINE_OFFSET_BASE_ADDRESS 0x0110 // page 1, address 10 to 1F, 8x int = 16 bytes
#define CURRENT_MODE_RANGE3_COARSE_GAIN_BASE_ADDRESS 0x0120 // page 1, address 20 to 2F, 8x int = 16 bytes
#define CURRENT_MODE_RANGE3_FINE_GAIN_BASE_ADDRESS 0x0130 // page 1, address 30 to 3F, 8x int = 16 bytes
// page 1,  address 40 to FF, 192 bytes --->  reserved for future use


#define FRAM_SOFT_LOCK_ADDRESS 0x0200 // page 2, address 0, 1 byte
#define USER_CONFIG_BASE_ADDRESS 0x0201 // page 2, address 1, 1 byte

// Note: STOREx TBD more accurately

#define STORE0_BASE_ADDRESS 0x0300 // page 3, address 00 to 7F, 128 bytes
#define STORE1_BASE_ADDRESS 0x0380 // page 3, address 80 to FF, 128 bytes
#define STORE2_BASE_ADDRESS 0x0400 // page 4, address 00 to 7F, 128 bytes
#define STORE3_BASE_ADDRESS 0x0480 // page 4, address 80 to FF, 128 bytes
#define STORE4_BASE_ADDRESS 0x0500 // page 5, address 00 to 7F, 128 bytes
#define STORE5_BASE_ADDRESS 0x0580 // page 5, address 80 to FF, 128 bytes
#define STORE6_BASE_ADDRESS 0x0600 // page 6, address 00 to 7F, 128 bytes
#define STORE7_BASE_ADDRESS 0x0680 // page 6, address 80 to FF, 128 bytes

/*
   boards base addresses for variables, effective address is MCP23xxx_ADDRESS + slot number
*/

#define MCP23xxx_ADDRESS 0x20

# define MCP23xxx_IODIR 0x00
# define MCP23xxx_IPOL 0x01
# define MCP23xxx_GPINTEN 0x02
# define MCP23xxx_DEFVAL 0x03
# define MCP23xxx_INTCON 0x04
# define MCP23xxx_IOCON 0x05
# define MCP23xxx_GPPU 0x06
# define MCP23xxx_INTF 0x07
# define MCP23xxx_INTCAP 0x08
# define MCP23xxx_GPIO  0x09
# define MCP23xxx_OLAT 0x0a

/*
   IVsource boards definitions
*/
#define IVSOURCE_LED_R 0xfe
#define IVSOURCE_LED_G 0x01
//#define IVSOURCE_JUMPERS_READ 0x06
//#define IVSOURCE_JUMPERS_READ 0x02
#define IVSOURCE_JUMPERS_READ 0x04 // IVsource V2
#define IVSOURCE_VSOURCE_ON 0b
#define IVSOURCE_VSOURCE_OFF
#define IVSOURCE_ISOURCE_ON
#define IVSOURCE_ISOURCE_OFF
#define IVSOURCE_VSOURCE_ON
#define IVSOURCE_ISOURCE_RANGE_0 0x1f
#define IVSOURCE_ISOURCE_RANGE_1 0xF9
#define IVSOURCE_ISOURCE_RANGE_2 0xFA
#define IVSOURCE_ISOURCE_RANGE_3 0xFC


/*
 * Misc
 */

 #define ABOUT_FIRST_LINE 10
 #define SERIAL_TIMEOUT 60000
