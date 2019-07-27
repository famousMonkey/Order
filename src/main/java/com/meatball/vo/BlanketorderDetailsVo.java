package com.meatball.vo;

import com.meatball.entity.Blanketorder;
import com.meatball.entity.Oilsorder;
import com.meatball.entity.Productorder;
import lombok.Data;

@Data
public class BlanketorderDetailsVo extends Blanketorder{
   Oilsorder oilsorder;
   Productorder productorder;
}
