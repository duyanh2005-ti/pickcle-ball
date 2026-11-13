package com.example.demo.Service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.demo.Entity.CourtsEntity;
import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.CourtRepository;
import com.example.demo.dto.CourtDTO;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class CourtServiceImplTest {

	@Mock private CourtRepository courtRepository;
	@Mock private ModelMapper modelMapper;
	@InjectMocks private CourtServiceImpl courtService;

	private static UsersEntity user(Long id, String role) {
		UsersEntity u = new UsersEntity();
		u.setId(id);
		u.setRole(role);
		return u;
	}

	private static CourtsEntity court(Long id, UsersEntity owner) {
		CourtsEntity c = new CourtsEntity();
		c.setId(id);
		c.setOwner(owner);
		return c;
	}

	@Test
	void suaSan_khongPhaiChuSan_bao403() {
		UsersEntity owner = user(1L, "COURT_OWNER");
		when(courtRepository.findById(10L)).thenReturn(Optional.of(court(10L, owner)));

		CustomUserDetails other = new CustomUserDetails(user(2L, "COURT_OWNER"));
		assertThatThrownBy(() -> courtService.updateCourt(10L, new CourtDTO(), other))
				.isInstanceOf(ForbiddenException.class);
		verify(courtRepository, never()).save(any());
	}

	@Test
	void suaSan_admin_duocSua() {
		CourtsEntity c = court(10L, user(1L, "COURT_OWNER"));
		when(courtRepository.findById(10L)).thenReturn(Optional.of(c));
		CourtDTO dto = new CourtDTO();
		dto.setName("Tên mới");

		courtService.updateCourt(10L, dto, new CustomUserDetails(user(99L, "ADMIN")));
		assertThat(c.getName()).isEqualTo("Tên mới");
		verify(courtRepository).save(c);
	}

	@Test
	void xoaNhieuSan_coMotSanCuaNguoiKhac_khongXoaSanNao() {
		UsersEntity me = user(1L, "COURT_OWNER");
		UsersEntity other = user(2L, "COURT_OWNER");
		when(courtRepository.findAllById(List.of(10L, 11L)))
				.thenReturn(List.of(court(10L, me), court(11L, other)));

		assertThatThrownBy(() -> courtService.deleteCourts(List.of(10L, 11L), new CustomUserDetails(me)))
				.isInstanceOf(ForbiddenException.class);
		verify(courtRepository, never()).deleteAll(any());
	}

	@Test
	void xoaSan_idKhongTonTai_bao404() {
		when(courtRepository.findAllById(List.of(10L, 12L))).thenReturn(List.of(court(10L, user(1L, "COURT_OWNER"))));
		assertThatThrownBy(() -> courtService.deleteCourts(List.of(10L, 12L),
				new CustomUserDetails(user(1L, "COURT_OWNER"))))
				.isInstanceOf(NotFoundException.class);
	}
}
