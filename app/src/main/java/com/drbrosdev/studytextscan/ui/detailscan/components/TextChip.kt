package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModel
import com.drbrosdev.studytextscan.persistence.entity.ExtractionModelType
import com.drbrosdev.studytextscan.ui.support.theme.*
import compose.icons.TablerIcons
import compose.icons.tablericons.At
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Link
import compose.icons.tablericons.Phone

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TextEntityChip(
    modifier: Modifier = Modifier,
    entity: ExtractionModel,
    onClick: () -> Unit = {},
) {
    val chipColor = when (entity.type) {
        ExtractionModelType.EMAIL -> ChipYellow
        ExtractionModelType.PHONE -> ChipGreen
        ExtractionModelType.URL -> ChipOrange
        ExtractionModelType.OTHER -> ChipBlue
    }

    val icon = when (entity.type) {
        ExtractionModelType.EMAIL -> TablerIcons.At
        ExtractionModelType.PHONE -> TablerIcons.Phone
        ExtractionModelType.URL -> TablerIcons.Link
        ExtractionModelType.OTHER -> TablerIcons.LetterCase
    }

    Surface(
        modifier = Modifier
            .requiredHeight(36.dp)
            .then(modifier),
        elevation = 0.dp,
        shape = CircleShape,
        onClick = onClick,
        color = chipColor
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = entity.content,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = size.minDimension / 1.65f
                        )
                    },
                imageVector = icon,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }

}

